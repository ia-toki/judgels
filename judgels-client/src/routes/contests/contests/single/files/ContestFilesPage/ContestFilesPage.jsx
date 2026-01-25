import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { ContestFileUploadCard } from '../ContestFileUploadCard/ContestFileUploadCard';
import { ContestFilesTable } from '../ContestFilesTable/ContestFilesTable';

import * as contestFileActions from '../modules/contestFileActions';

export default function ContestFilesPage() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const refreshFiles = async () => {
    const data = await dispatch(contestFileActions.getFiles(contest.jid));
    setState({
      response: data,
    });
  };

  useEffect(() => {
    refreshFiles();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Files</h3>
        <hr />
        {renderUploadCard()}
        {renderFiles()}
      </ContentCard>
    );
  };

  const renderUploadCard = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canManage) {
      return null;
    }
    return <ContestFileUploadCard onSubmit={uploadFile} />;
  };

  const renderFiles = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: files } = response;
    if (files.length === 0) {
      return (
        <p>
          <small>No files.</small>
        </p>
      );
    }

    return <ContestFilesTable contest={contest} files={files} />;
  };

  const uploadFile = async data => {
    await dispatch(contestFileActions.uploadFile(contest.jid, data.file));
    await refreshFiles();
  };

  return render();
}
