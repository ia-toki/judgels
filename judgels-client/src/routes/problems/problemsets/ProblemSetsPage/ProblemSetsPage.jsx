import { parse } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { ProblemSetCard } from '../ProblemSetCard/ProblemSetCard';

import * as problemSetActions from '../modules/problemSetActions';

const PAGE_SIZE = 20;

export default function ProblemSetsPage() {
  const location = useLocation();
  const dispatch = useDispatch();

  const queries = parse(location.search);
  const archiveSlug = queries.archive;
  const name = queries.name;

  const [state, setState] = useState({
    response: undefined,
    isFilterLoading: false,
  });

  useEffect(() => {
    if (archiveSlug || name) {
      setState(prevState => ({ ...prevState, isFilterLoading: true }));
    }
  }, [archiveSlug, name]);

  const render = () => {
    return (
      <Card title="Browse problemsets">
        {renderHeader()}
        {renderProblemSets()}
        {renderPagination()}
      </Card>
    );
  };

  const renderHeader = () => {
    return (
      <>
        <div className="float-right">{renderFilter()}</div>
        <div className="clearfix" />
        <div className="content-card__section">
          {renderFilterResultsBanner()}
          <hr />
        </div>
      </>
    );
  };

  const renderFilterResultsBanner = () => {
    if (!archiveSlug && !name) {
      return <>Most recently added problemsets:</>;
    }

    const { response } = state;
    const archiveName = response && response.archiveName;

    if (archiveName && !name) {
      return (
        <>
          Problemsets in archive <b>{archiveName}</b>:
        </>
      );
    }
    if (!name) {
      return null;
    }

    const archiveNameResult = archiveName ? (
      <>
        {' '}
        in archive <b>{archiveName}:</b>
      </>
    ) : (
      ''
    );

    return (
      <>
        Search results for: <b>{name}</b>
        {archiveNameResult}
      </>
    );
  };

  const renderFilter = () => {
    return (
      <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={name || ''} isLoading={state.isFilterLoading} />
    );
  };

  const renderProblemSets = () => {
    const { response } = state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data, archiveDescriptionsMap, problemSetProgressesMap, profilesMap } = response;
    if (!data) {
      return <LoadingContentCard />;
    }

    if (data.page.length === 0) {
      return (
        <p>
          <small>No problemsets.</small>
        </p>
      );
    }
    return data.page.map(problemSet => (
      <ProblemSetCard
        key={problemSet.jid}
        problemSet={problemSet}
        archiveDescription={archiveDescriptionsMap[problemSet.archiveJid]}
        progress={problemSetProgressesMap[problemSet.jid]}
        profilesMap={profilesMap}
      />
    ));
  };

  const renderPagination = () => {
    return <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} key={'' + archiveSlug + name} />;
  };

  const onChangePage = async nextPage => {
    if (state.response) {
      setState(prevState => ({ ...prevState, response: { ...state.response, data: undefined } }));
    }
    const response = await dispatch(problemSetActions.getProblemSets(archiveSlug, name, nextPage));
    setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  const searchBoxUpdateQueries = (name, queries) => {
    return { ...queries, page: undefined, name };
  };

  return render();
}
