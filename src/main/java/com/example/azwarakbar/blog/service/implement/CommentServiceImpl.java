package com.example.azwarakbar.blog.service.implement;

import com.example.azwarakbar.blog.exception.BlogException;
import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.model.Comment;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.RoleName;
import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.repository.CommentRepository;
import com.example.azwarakbar.blog.repository.PostRepository;
import com.example.azwarakbar.blog.repository.UserRepository;
import com.example.azwarakbar.blog.schema.CommentRequest;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.service.CommentService;
import com.example.azwarakbar.blog.util.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
	private static final String COMMENT_DOES_NOT_BELONG_TO_POST = "Comment does not belong to post";

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public PagedResponse<Comment> getAllComments(Long postId, int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
		Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

		return new PagedResponse<>(comments.getContent(), comments.getNumber(), (int)comments.getTotalElements());
	}

	@Override
	public Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		User user = userRepository.getUser(currentUser);
		Comment comment = new Comment(commentRequest.getBody());
		comment.setUser(user);
		comment.setPost(post);
		comment.setUser(user);
		return commentRepository.save(comment);
	}

	@Override
	public Comment getComment(Long postId, Long id) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
		if (comment.getPost().getId().equals(post.getId())) {
			return comment;
		}

		throw new BlogException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
	}

	@Override
	public Comment updateComment(Long postId, Long id, CommentRequest commentRequest,
			UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

		if (!comment.getPost().getId().equals(post.getId())) {
			throw new BlogException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			comment.setBody(commentRequest.getBody());
			return commentRepository.save(comment);
		}

		throw new BlogException(HttpStatus.UNAUTHORIZED,  "You don't have permission to update this comment");
	}

	@Override
	public MessageResponse deleteComment(Long postId, Long id, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

		if (!comment.getPost().getId().equals(post.getId())) {
			return new MessageResponse(Boolean.FALSE, COMMENT_DOES_NOT_BELONG_TO_POST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			commentRepository.deleteById(comment.getId());
			return new MessageResponse(Boolean.TRUE, "You successfully deleted comment");
		}

		throw new BlogException(HttpStatus.UNAUTHORIZED, "You don't have permission to delete this comment");
	}
}
