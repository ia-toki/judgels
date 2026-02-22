import { Radio, RadioGroup } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useLocation, useNavigate } from '@tanstack/react-router';
import classNames from 'classnames';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { sendGAEvent } from '../../../../ga';
import { archivesQueryOptions } from '../../../../modules/queries/archive';

import './ProblemSetArchiveFilter.scss';

export default function ProblemSetArchiveFilter() {
  const location = useLocation();
  const navigate = useNavigate();

  const archiveSlug = location.search.archive || '';

  const { data: response } = useQuery(archivesQueryOptions());

  const renderArchiveCategories = () => {
    const archives = [{ slug: '', name: '(All problemsets)', category: '' }, ...response.data];
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
    navigate({
      search: {
        ...location.search,
        name: undefined,
        page: undefined,
        archive: archiveSlug === '' ? undefined : archiveSlug,
      },
    });

    sendGAEvent({
      category: 'Problems',
      action: 'Filter archive',
      label: archiveSlug,
    });
  };

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
}
