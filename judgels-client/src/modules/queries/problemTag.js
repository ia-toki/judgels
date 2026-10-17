import { queryOptions } from '@tanstack/react-query';

import { problemTagAPI } from '../api/problemTag';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemsQueryOptions } from './problem';

export const problemAllTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['problems', 'tags'],
    queryFn: () => problemTagAPI.getAllTags(getToken()),
  });

export const problemAllTopicTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['problems', 'topic-tags'],
    queryFn: () => problemTagAPI.getAllTopicTags(getToken()),
  });

export const problemTopicTagsQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'topic-tags'],
    queryFn: () => problemTagAPI.getTopicTags(getToken(), problemJid),
  });

// The problems list is filtered by tag, and its key is a prefix of the tag counts' key.
export const setProblemTopicTagsMutationOptions = problemJid => ({
  mutationFn: topicTags => problemTagAPI.setTopicTags(getToken(), problemJid, topicTags),
  onSuccess: () => {
    queryClient.invalidateQueries(problemsQueryOptions());
    queryClient.invalidateQueries(problemTopicTagsQueryOptions(problemJid));
  },
});
