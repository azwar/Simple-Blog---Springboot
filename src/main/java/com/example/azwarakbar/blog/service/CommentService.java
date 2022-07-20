package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.Comment;
import com.example.azwarakbar.blog.schema.CommentRequest;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.util.PagedResponse;

public interface CommentService {

	PagedResponse<Comment> getAllComments(Long postId, int page);

	Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser);

	Comment getComment(Long postId, Long id);

	Comment updateComment(Long postId, Long id, CommentRequest commentRequest, UserPrincipal currentUser);

	MessageResponse deleteComment(Long postId, Long id, UserPrincipal currentUser);

}
