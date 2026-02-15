import { useLocation } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../components/LoadingState/LoadingState';
import Pagination from '../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { callAction } from '../../../modules/callAction';
import { useSession } from '../../../modules/session';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

import * as submissionActions from '../modules/submissionActions';

const PAGE_SIZE = 20;

export default function SubmissionsPage() {
  const location = useLocation();
  const { user } = useSession();
  const userJid = user?.jid;
  const username = user?.username;

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    // return (
    //   <>
    //     {renderUserFilter()}
    //     {renderSubmissions()}
    //     {renderPagination()}
    //   </>
    // );

    return <small>This page is under maintenance.</small>;
  };

  const renderUserFilter = () => {
    return userJid && <SubmissionUserFilter />;
  };

  const isUserFilterMine = () => {
    return (location.pathname + '/').includes('/mine/');
  };

  const renderSubmissions = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      profilesMap,
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
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
        onRegrade={onRegrade}
      />
    );
  };

  const renderPagination = () => {
    return <Pagination key={'' + isUserFilterMine()} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const refreshSubmissions = async page => {
    const usernameFilter = isUserFilterMine() ? username : undefined;
    const response = await callAction(submissionActions.getSubmissions(undefined, usernameFilter, undefined, page));
    setState({ response });
    return response.data;
  };

  const onRegrade = async submissionJid => {
    await callAction(submissionActions.regradeSubmission(submissionJid));
    await refreshSubmissions(location.search.page);
  };

  return render();
}
