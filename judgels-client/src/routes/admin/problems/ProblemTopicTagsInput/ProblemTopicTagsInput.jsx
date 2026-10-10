import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormTableInput } from '../../../../components/forms/FormTableInput/FormTableInput';

import './ProblemTopicTagsInput.scss';

const TOPIC_PREFIX = 'topic-';
const CHILD_SEPARATOR = ': ';

function isChildOf(tag, parent) {
  return tag.startsWith(parent + CHILD_SEPARATOR);
}

function getParent(tag) {
  const pos = tag.indexOf(CHILD_SEPARATOR);
  return pos === -1 ? undefined : tag.substring(0, pos);
}

function getTagName(tag) {
  const parent = getParent(tag);
  return parent ? tag.substring(parent.length + CHILD_SEPARATOR.length) : tag.substring(TOPIC_PREFIX.length);
}

export function ProblemTopicTagsInput(props) {
  const { input, topicTags } = props;
  const selectedTags = input.value || [];

  const hasSelectedChild = tag => selectedTags.some(t => isChildOf(t, tag));

  const changeTag = e => {
    const tag = e.target.name;

    let newTags = selectedTags.filter(t => t !== tag && !isChildOf(t, tag));
    if (e.target.checked) {
      const parent = getParent(tag);
      newTags = [...new Set([...newTags, tag, ...(parent ? [parent] : [])])];
    }
    input.onChange(newTags);
  };

  return (
    <FormTableInput {...props}>
      {topicTags.map(tag => {
        const parent = getParent(tag);
        const checked = selectedTags.includes(tag);
        return (
          <Checkbox
            key={tag}
            name={tag}
            className={classNames('problem-topic-tags-input__option', {
              'problem-topic-tags-input__option-child': !!parent,
            })}
            label={getTagName(tag)}
            checked={checked}
            indeterminate={checked && hasSelectedChild(tag)}
            disabled={!!parent && selectedTags.includes(parent) && !hasSelectedChild(parent)}
            onChange={changeTag}
          />
        );
      })}
    </FormTableInput>
  );
}
