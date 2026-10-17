import { Checkbox } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';
import { useLocation, useNavigate } from '@tanstack/react-router';
import { useMemo, useState } from 'react';

import { ContentCard } from '../ContentCard/ContentCard';

import './ProblemTagFilter.scss';

const parseTags = queryTags => {
  let tags = queryTags || [];
  if (typeof tags === 'string') {
    tags = [tags];
  }
  return tags;
};

export default function ProblemTagFilter({ queryOptions }) {
  const location = useLocation();
  const navigate = useNavigate();

  const tags = parseTags(location.search.tags);

  const [selectedTags, setSelectedTags] = useState(tags);

  const { data: response } = useQuery(queryOptions);

  const allTags = useMemo(() => {
    if (!response) {
      return [];
    }
    return response.data.flatMap(c => c.options.map(opt => opt.value));
  }, [response]);

  const renderAvailableTags = () => {
    if (!response) {
      return null;
    }

    const { data: problemTags } = response;
    return problemTags.map(category => renderTagCategory(category));
  };

  const renderTagCategory = ({ title, options }) => {
    return (
      <div key={title}>
        <h5>{title}</h5>
        {options.map(opt => (
          <Box key={opt.value} asChild {...(isTagChild(opt.value) && { marginLeft: 6 })}>
            <Checkbox
              name={opt.value}
              className="problem-tag-filter__option"
              label={getTagName(opt) + ' (' + opt.count + ')'}
              checked={isTagSelected(opt.value)}
              indeterminate={isTagChildSelected(opt.value)}
              disabled={isTagParentSelected(opt.value)}
              onChange={changeTag}
            />
          </Box>
        ))}
      </div>
    );
  };

  const isTagSelected = tag => {
    return selectedTags.includes(tag);
  };

  const isTagParentSelected = tag => {
    return selectedTags.some(t => t !== tag && tag.startsWith(t));
  };

  const isTagChildSelected = tag => {
    return selectedTags.some(t => t !== tag && t.startsWith(tag));
  };

  const isTagChild = tag => {
    return tag.includes(': ');
  };

  const getTagName = opt => {
    return isTagChild(opt.value) ? opt.label.split(': ')[1] : opt.label;
  };

  const sanitizeTags = tags => {
    return tags.filter(t => allTags.includes(t));
  };

  const changeTag = e => {
    const tag = e.target.name;
    const checked = e.target.checked;

    let newTags = selectedTags;
    if (checked) {
      newTags = [...new Set([...newTags, tag])]
        .filter(t => !(t !== tag && t.startsWith(tag)))
        .filter(t => !(t !== tag && tag.startsWith(t)));
    } else {
      let s = new Set(newTags);
      s.delete(tag);
      newTags = [...s];
    }

    newTags = sanitizeTags(newTags);

    navigate({
      search: {
        ...location.search,
        tags: newTags,
        page: 1,
      },
    });

    setSelectedTags(newTags);
  };

  return (
    <ContentCard
      header={
        <>
          <h4>Filter problem</h4>
          <hr />
        </>
      }
    >
      {renderAvailableTags()}
    </ContentCard>
  );
}
