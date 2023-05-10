import { Tag, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import { connect } from 'react-redux';

import { selectShowProblemTopicTags } from '../../modules/webPrefs/webPrefsSelectors';

import './ProblemTopicTags.scss';

function formatTagName(tag) {
  return tag.substring('topic-'.length);
}

function isParent(tag, tags) {
  return tags.some(t => t !== tag && t.startsWith(tag));
}

function ProblemTopicTags({ showProblemTopicTags, tags, alignLeft }) {
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

const mapStateToProps = state => ({
  showProblemTopicTags: selectShowProblemTopicTags(state),
});

export default connect(mapStateToProps)(ProblemTopicTags);
