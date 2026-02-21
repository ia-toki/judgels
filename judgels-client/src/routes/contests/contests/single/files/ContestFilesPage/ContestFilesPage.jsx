import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestFilesQueryOptions } from '../../../../../../modules/queries/contestFile';
import { ContestFileUploadCard } from '../ContestFileUploadCard/ContestFileUploadCard';
import { ContestFilesTable } from '../ContestFilesTable/ContestFilesTable';

export default function ContestFilesPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: response } = useQuery(contestFilesQueryOptions(contest.jid));

  const renderUploadCard = () => {
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canManage) {
      return null;
    }
    return <ContestFileUploadCard contest={contest} />;
  };

  const renderFiles = () => {
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

  return (
    <ContentCard>
      <h3>Files</h3>
      <hr />
      {renderUploadCard()}
      {renderFiles()}
    </ContentCard>
  );
}
