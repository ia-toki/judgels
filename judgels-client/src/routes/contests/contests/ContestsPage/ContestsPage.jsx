import { parse } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation } from 'react-router-dom';

import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';

import * as contestActions from '../modules/contestActions';

const PAGE_SIZE = 20;

export default function ContestsPage() {
  const location = useLocation();
  const dispatch = useDispatch();

  const queries = parse(location.search);
  const name = queries.name;

  const [state, setState] = useState({
    response: undefined,
    isFilterLoading: false,
  });

  useEffect(() => {
    if (name) {
      setState(prevState => ({ ...prevState, isFilterLoading: true }));
    }
  }, [name]);

  const render = () => {
    return (
      <Card title="Contests">
        {renderHeader()}
        {renderContests()}
        {renderPagination()}
      </Card>
    );
  };

  const renderHeader = () => {
    return (
      <>
        <div className="float-left">{renderCreateDialog()}</div>
        <div className="float-right">{renderFilter()}</div>
        <div className="clearfix" />
        {renderFilterResultsBanner()}
      </>
    );
  };

  const renderFilterResultsBanner = () => {
    if (!name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Search results for: <b>{name}</b>
        <hr />
      </div>
    );
  };

  const renderFilter = () => {
    return (
      <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={name || ''} isLoading={state.isFilterLoading} />
    );
  };

  const createContest = data => dispatch(contestActions.createContest(data));

  const renderCreateDialog = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const config = response.config;
    if (!config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog onCreateContest={createContest} />;
  };

  const renderContests = () => {
    const { response } = state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const rolesMap = response.rolesMap;
    const contests = response.data;
    if (!contests) {
      return <LoadingContentCard />;
    }

    if (contests.page.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  const renderPagination = () => {
    return <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} key={name || ''} />;
  };

  const onChangePage = async nextPage => {
    if (state.response) {
      setState(prevState => ({ ...prevState, response: { ...state.response, data: undefined } }));
    }
    const response = await dispatch(contestActions.getContests(name, nextPage));
    setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  const searchBoxUpdateQueries = (name, queries) => {
    return { ...queries, page: undefined, name };
  };

  return render();
}
