import { Checkbox } from '@blueprintjs/core';
import { useLocation, useNavigate } from '@tanstack/react-router';
import classNames from 'classnames';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { callAction } from '../../../../modules/callAction';

import * as problemActions from '../modules/problemActions';

import './ProblemTagFilter.scss';

export default function ProblemTagFilter() {
  const location = useLocation();
  const navigate = useNavigate();

  const parseTags = queryTags => {
    let tags = queryTags || [];
    if (typeof tags === 'string') {
      tags = [tags];
    }
    return tags;
  };

  const tags = parseTags(location.search.tags);

  const [state, setState] = useState({
    tags,
    response: undefined,
  });

  const loadTags = async () => {
    const response = await callAction(problemActions.getProblemTags());
    const allTags = [].concat(response.data.map(c => c.options.map(opt => opt.value))).flat();
    setState(prevState => ({ ...prevState, response, allTags }));
  };

  useEffect(() => {
    loadTags();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h4>Filter problem</h4>
        <hr />
        {renderAvailableTags()}
      </ContentCard>
    );
  };

  const renderAvailableTags = () => {
    const { response } = state;
    if (!response) {
      return null;
    }

    const { data: problemTags } = response;
    return problemTags.map(category => renderTagCategory(category));
  };

  const renderTagCategory = ({ title, options }) => {
    return (
      <div key={title}>
        <h5 className="problem-tag-filter__category">{title}</h5>
        {options.map(opt => (
          <Checkbox
            key={opt.value}
            name={opt.value}
            className={classNames('problem-tag-filter__option', {
              'problem-tag-filter__option-child': isTagChild(opt.value),
            })}
            label={getTagName(opt) + ' (' + opt.count + ')'}
            checked={isTagSelected(opt.value)}
            indeterminate={isTagChildSelected(opt.value)}
            disabled={isTagParentSelected(opt.value)}
            onChange={changeTag}
          />
        ))}
      </div>
    );
  };

  const isTagSelected = tag => {
    return state.tags.includes(tag);
  };

  const isTagParentSelected = tag => {
    return state.tags.some(t => t !== tag && tag.startsWith(t));
  };

  const isTagChildSelected = tag => {
    return state.tags.some(t => t !== tag && t.startsWith(tag));
  };

  const isTagChild = tag => {
    return tag.includes(': ');
  };

  const getTagName = opt => {
    return isTagChild(opt.value) ? opt.label.split(': ')[1] : opt.label;
  };

  const changeTag = e => {
    const tag = e.target.name;
    const checked = e.target.checked;

    let tags = state.tags;
    if (checked) {
      tags = [...new Set([...tags, tag])]
        .filter(t => !(t !== tag && t.startsWith(tag)))
        .filter(t => !(t !== tag && tag.startsWith(t)));
    } else {
      let s = new Set(tags);
      s.delete(tag);
      tags = [...s];
    }

    tags = sanitizeTags(tags);

    navigate({
      search: {
        ...location.search,
        tags,
        page: 1,
      },
    });

    setState(prevState => ({ ...prevState, tags }));
  };

  const sanitizeTags = tags => {
    return tags.filter(t => state.allTags.includes(t));
  };

  return render();
}
