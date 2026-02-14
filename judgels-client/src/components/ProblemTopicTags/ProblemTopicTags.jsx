import { Intent, Tag } from '@blueprintjs/core';
import classNames from 'classnames';

import { useWebPrefs } from '../../modules/webPrefs';

import './ProblemTopicTags.scss';

function formatTagName(tag) {
  return tag.substring('topic-'.length);
}

function isParent(tag, tags) {
  return tags.some(t => t !== tag && t.startsWith(tag));
}

export default function ProblemTopicTags({ tags, alignLeft }) {
  const { showProblemTopicTags } = useWebPrefs();

  if (!showProblemTopicTags) {
    return null;
  }

  return tags
    .filter(tag => tag.startsWith('topic-'))
    .filter(tag => !isParent(tag, tags))
    .sort()
    .map(formatTagName)
    .map(tag => (
      <Tag
        round
        multiline
        intent={Intent.PRIMARY}
        key={tag}
        className={classNames({ 'problem-topic-tag': !alignLeft, 'problem-topic-tag-left': alignLeft })}
      >
        {tag}
      </Tag>
    ));
}
