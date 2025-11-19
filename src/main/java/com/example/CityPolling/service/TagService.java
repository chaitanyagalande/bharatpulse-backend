package com.example.CityPolling.service;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.PollTag;
import com.example.CityPolling.model.PollTagId;
import com.example.CityPolling.model.Tag;
import com.example.CityPolling.repository.PollTagRepository;
import com.example.CityPolling.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PollTagRepository pollTagRepository;

    // Called after poll is created
    public void processTagsForPoll(List<String> tags, Poll poll) {

        // User provides no tags case
        if (tags == null || tags.isEmpty()) {
            return;
        }

        String city = poll.getCity();

        for (String tagName : tags) {

            String clean = tagName.trim().toLowerCase();

            // Find or create Tag
            Tag tag = tagRepository.findByNameAndCity(clean, city)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(clean);
                        newTag.setCity(city);
                        newTag.setUsageCount(0L);
                        return tagRepository.save(newTag);
                    });

            // increment usage
            tag.setUsageCount(tag.getUsageCount() + 1);
            tagRepository.save(tag);

            // Create PollTag mapping
            PollTag pollTag = new PollTag();
            pollTag.setId(new PollTagId(poll.getId(), tag.getId()));
            pollTag.setPoll(poll);
            pollTag.setTag(tag);

            pollTagRepository.save(pollTag);
        }
    }

    // Called after poll is deleted
    @Transactional
    public void deleteTagsForPoll(Long pollId) {

        // 1. Fetch all PollTag relations
        List<PollTag> pollTags = pollTagRepository.findById_PollId(pollId);

        if (pollTags.isEmpty()) return;

        // 2. Extract unique Tags (avoid duplicates if same tag used multiple times)
        Set<Long> tagIds = pollTags.stream()
                .map(pt -> pt.getTag().getId())
                .collect(Collectors.toSet());

        // 3. Delete all PollTag relations first (remove FK constraints)
        pollTagRepository.deleteAll(pollTags);
        pollTagRepository.flush();  // ensures immediate DB update

        // 4. Process each tag safely
        for (Long tagId : tagIds) {

            Tag tag = tagRepository.findById(tagId).orElse(null);
            if (tag == null) continue;

            long newCount = Math.max(0, tag.getUsageCount() - 1);
            tag.setUsageCount(newCount);

            if (newCount <= 0) {
                // safe to delete now because no PollTag references it anymore
                tagRepository.delete(tag);
            } else {
                tagRepository.save(tag);
            }
        }
    }



    // Used while getting tags for a particular poll while displaying
    public List<String> getTagNamesForPoll(Long pollId) {
        return pollTagRepository.findById_PollId(pollId)
                .stream()
                .map(pt -> pt.getTag().getName())
                .toList();
    }

    public List<Tag> findByCityOrderByUsageCountDesc(String city) {
        return tagRepository.findByCityOrderByUsageCountDesc(city);
    }
}
