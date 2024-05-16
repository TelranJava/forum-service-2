package telran.java52.post.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java52.model.Comment;
import telran.java52.model.Post;
import telran.java52.post.dao.PostRepository;
import telran.java52.post.dto.DatePeriodDto;
import telran.java52.post.dto.NewCommentDto;
import telran.java52.post.dto.NewPostDto;
import telran.java52.post.dto.PostDto;
import telran.java52.post.dto.exeption.PostNotFoundExeption;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	final PostRepository postRepository; // создается за счет @RequiredArgsConstructor
	final ModelMapper modelMapper;

	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		post = postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundExeption::new);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto removePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundExeption::new);
		postRepository.deleteById(id);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundExeption::new);
		if (newPostDto.getTitle() != null) {
			post.setTitle(newPostDto.getTitle());
		}
		if (newPostDto.getContent() != null) {
			post.setContent(newPostDto.getContent());
		}
		if (newPostDto.getTags().size() > 0) {
			newPostDto.getTags().forEach(post::addTag);
		}
		post = postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundExeption::new);
		if (newCommentDto.getMessage() != null && author != null) {
			post.addComment(new Comment(author, newCommentDto.getMessage()));
		}
		post = postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public void addLike(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundExeption::new);
		post.addLike();
		postRepository.save(post);
	}

	@Override
	public Iterable<PostDto> findPostsByAuthor(String author) {// hw
		return postRepository.findByAuthorIgnoreCase(author)
				.map(p -> modelMapper.map(p, PostDto.class))
				.toList();
	}
	

	@Override
	public Iterable<PostDto> findPostsByTags(List<String> tags) {// hw
		return postRepository.findPostsByTagsIn(tags)
				.map(p -> modelMapper.map(p, PostDto.class))
				.toList();
	}

	@Override
	public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriod) {// hw
		return postRepository.findPostsByDateCreatedAfterAndBefore(datePeriod.getDateFrom(), datePeriod.getDateTo())
				.map(p->modelMapper.map(p, PostDto.class))
				.toList();
	}

}
