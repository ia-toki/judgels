import { Radio, RadioGroup } from '@blueprintjs/core';
import classNames from 'classnames';
import { parse, stringify } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation, useNavigate } from 'react-router-dom';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { sendGAEvent } from '../../../../ga';

import * as archiveActions from '../modules/archiveActions';

import './ProblemSetArchiveFilter.scss';

export default function ProblemSetArchiveFilter() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const queries = parse(location.search);
  const archiveSlug = queries.archive || '';

  const [state, setState] = useState({
    response: undefined,
    archiveSlug,
  });

  const loadArchives = async () => {
    const response = await dispatch(archiveActions.getArchives());
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    loadArchives();
  }, []);

  const render = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    return (
      <ContentCard>
        <h4>Filter problemset</h4>
        <hr />
        {renderArchiveCategories()}
      </ContentCard>
    );
  };

  const renderArchiveCategories = () => {
    const archives = [{ slug: '', name: '(All problemsets)', category: '' }, ...state.response.data];
    const archivesByCategory = {};
    archives.forEach(archive => {
      if (archivesByCategory[archive.category]) {
        archivesByCategory[archive.category] = [...archivesByCategory[archive.category], archive];
      } else {
        archivesByCategory[archive.category] = [archive];
      }
    });

    const categories = Object.keys(archivesByCategory).sort();
    return categories.map(category => renderArchives(category, archivesByCategory[category]));
  };

  const renderArchives = (category, archives) => {
    return (
      <div key={category}>
        {category && <p className="archive-filter__category">{category}</p>}
        <RadioGroup key={category} name="archiveSlug" onChange={changeArchive} selectedValue={archiveSlug}>
          {archives.map(archive => (
            <Radio key={archive.slug} labelElement={renderArchiveOption(archive)} value={archive.slug} />
          ))}
        </RadioGroup>
      </div>
    );
  };

  const renderArchiveOption = archive => {
    return (
      <span className={classNames({ 'archive-filter__option--inactive': archive.slug !== archiveSlug })}>
        {archive.name}
      </span>
    );
  };

  const changeArchive = e => {
    const archiveSlug = e.target.value;
    const queries = parse(location.search);
    navigate({
      search: stringify({
        ...queries,
        name: undefined,
        page: undefined,
        archive: archiveSlug === '' ? undefined : archiveSlug,
      }),
    });
    setState(prevState => ({ ...prevState, archiveSlug }));

    sendGAEvent({
      category: 'Problems',
      action: 'Filter archive',
      label: archiveSlug,
    });
  };

  return render();
}
