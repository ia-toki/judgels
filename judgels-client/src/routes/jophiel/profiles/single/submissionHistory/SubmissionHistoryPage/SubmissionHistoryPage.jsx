import { useParams } from '@tanstack/react-router';
import { useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../../../components/Card/Card';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

import * as profileActions from '../../modules/profileActions';

export default function SubmissionHistoryPage() {
  const { username } = useParams({ strict: false });
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    return (
      <Card title="Submission history">
        {renderSubmissions()}
        {renderPagination()}
      </Card>
    );
  };

  const renderSubmissions = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
      />
    );
  };

  const renderPagination = () => {
    return <Pagination key={1} pageSize={20} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const refreshSubmissions = async page => {
    const response = await dispatch(profileActions.getSubmissions(username, page));
    setState(prevState => ({ ...prevState, response }));
    return response.data;
  };

  return render();
}
