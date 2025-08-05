package darak.community.service.post;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.AttachmentRepository;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostRepository;
import darak.community.domain.post.UploadFile;
import darak.community.service.post.request.AttachmentCreateServiceRequest;
import darak.community.service.post.response.AttachmentResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileUploadService fileUploadService;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public List<AttachmentResponse> addAttachments(AttachmentCreateServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        List<UploadFile> uploadFiles = fileUploadService.uploadAttachments(request.getFiles());

        List<AttachmentResponse> responses = new ArrayList<>();
        for (UploadFile uploadFile : uploadFiles) {
            Attachment attachment = Attachment.builder()
                    .post(post)
                    .fileName(uploadFile.getFileName())
                    .fileType(uploadFile.getFileType())
                    .url(uploadFile.getUrl())
                    .size(uploadFile.getSize())
                    .build();
            attachmentRepository.save(attachment);
            responses.add(AttachmentResponse.from(attachment));
        }
        return responses;
    }

    @Override
    @Transactional
    public void delete(Long attachmentId) {
        attachmentRepository.delete(findAttachmentById(attachmentId));
    }

    private Attachment findAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 첨부파일입니다."));
    }

    @Override
    public List<AttachmentResponse> findAttachmentsByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId).stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    @Override
    public List<AttachmentResponse> findImagesByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId).stream()
                .filter(Attachment::isImage)
                .map(AttachmentResponse::from)
                .toList();
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private Attachment findAttachmentByAttachmentId(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 첨부파일입니다."));
    }
}
