import { Intent, Tag } from '@blueprintjs/core';

import { useWebPrefs } from '../../modules/webPrefs';

import './ProblemTopicTags.scss';

function formatTagName(tag) {
  return tag.substring('topic-'.length);
}

function isParent(tag, tags) {
  return tags.some(t => t !== tag && t.startsWith(tag));
}

export function getDisplayedTopicTags(tags) {
  return tags
    .filter(tag => tag.startsWith('topic-'))
    .filter(tag => !isParent(tag, tags))
    .sort()
    .map(formatTagName);
}

export function ProblemTopicTagList({ className, tags }) {
  const { showProblemTopicTags } = useWebPrefs();

  if (!showProblemTopicTags) {
    return null;
  }

  return getDisplayedTopicTags(tags).map(tag => (
    <Tag round multiline intent={Intent.PRIMARY} key={tag} className={className}>
      {tag}
    </Tag>
  ));
}

export default function ProblemTopicTags({ tags }) {
  return (
    <div>
      <ProblemTopicTagList tags={tags} className="problem-topic-tag" />
    </div>
  );
}
