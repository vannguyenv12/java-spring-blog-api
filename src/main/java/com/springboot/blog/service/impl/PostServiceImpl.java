package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFound;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        // convert dto to entity and save to db
        Post post = mapToEntity(postDto);

        Post savedPost = postRepository.save(post);

        // convert entity to dto to show for client
        PostDto postResource = mapToDto(savedPost);

        return postResource;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =  PageRequest.of(pageNo, pageSize, sort);
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content = listOfPosts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Post", "id", id));

        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        // get entity by id
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Post", "id", id));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());

        Post savedPost = postRepository.save(post);

        return mapToDto(savedPost);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Post", "id", id));

        postRepository.delete(post);
    }

    private PostDto mapToDto(Post post) {
        PostDto postResource = mapper.map(post, PostDto.class);
//        PostDto postResource = new PostDto();
//        postResource.setId(post.getId());
//        postResource.setTitle(post.getTitle());
//        postResource.setContent(post.getContent());
//        postResource.setDescription(post.getDescription());

        return postResource;
    }

    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);

//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());

        return post;
    }
}
