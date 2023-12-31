package org.bssm.attachit.domain.attachment.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bssm.attachit.domain.attachment.domain.Attachment;
import org.bssm.attachit.domain.attachment.presentation.dto.request.PostAttachmentRequest;
import org.bssm.attachit.domain.attachment.repository.AttachmentRepository;
import org.bssm.attachit.domain.user.domain.User;
import org.bssm.attachit.domain.user.exception.UserNotFoundException;
import org.bssm.attachit.domain.user.repository.UserRepository;
import org.bssm.attachit.global.jwt.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostAttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final FileSaveUtil fileSaveUtil;

    public ResponseEntity<String> execute(PostAttachmentRequest request, MultipartFile file, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(jwtUtil.extractEmail(httpServletRequest)).orElseThrow(
                () -> UserNotFoundException.EXCEPTION
        );

        String path = null;

        if (!file.isEmpty()) {
            path = fileSaveUtil.save(file);
        }

        attachmentRepository.save(
                Attachment.builder()
                        .path(path)
                        .content(request.getContent())
                        .user(user)
                        .colorCode(request.getColorCode())
                        .zIndex(request.getZ())
                        .postType(request.getPostType())
                        .xPosition(request.getX())
                        .yPosition(request.getY())
                        .build()
        );

        return ResponseEntity.ok("success");
    }
}
