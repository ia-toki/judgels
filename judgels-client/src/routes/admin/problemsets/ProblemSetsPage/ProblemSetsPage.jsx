import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { problemSetsQueryOptions } from '../../../../modules/queries/problemSet';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';
import { ProblemSetEditDialog } from '../ProblemSetEditDialog/ProblemSetEditDialog';
import { ProblemSetProblemEditDialog } from '../ProblemSetProblemEditDialog/ProblemSetProblemEditDialog';
import { ProblemSetsTable } from '../ProblemSetsTable/ProblemSetsTable';

const PAGE_SIZE = 20;

export default function ProblemSetsPage() {
  const location = useLocation();
  const page = location.search.page;

  const [editedProblemSet, setEditedProblemSet] = useState(undefined);
  const [editDialogType, setEditDialogType] = useState(undefined);

  const { data: response } = useQuery(problemSetsQueryOptions({ page }));

  const editProblemSet = problemSet => {
    setEditedProblemSet(problemSet);
    setEditDialogType(problemSet ? 'edit' : undefined);
  };

  const editProblemSetProblems = problemSet => {
    setEditedProblemSet(problemSet);
    setEditDialogType(problemSet ? 'problems' : undefined);
  };

  const closeDialog = () => {
    setEditedProblemSet(undefined);
    setEditDialogType(undefined);
  };

  const archiveSlug = response && editedProblemSet && response.archiveSlugsMap[editedProblemSet.archiveJid];

  const renderProblemSets = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problemSets, archiveSlugsMap } = response;
    if (problemSets.page.length === 0) {
      return (
        <p>
          <small>No problem sets.</small>
        </p>
      );
    }

    return (
      <ProblemSetsTable
        problemSets={problemSets.page}
        archiveSlugsMap={archiveSlugsMap}
        onEditProblemSet={editProblemSet}
        onEditProblemSetProblems={editProblemSetProblems}
      />
    );
  };

  return (
    <ContentCard>
      <h3>Problemsets</h3>
      <hr />
      <ProblemSetCreateDialog />
      <ProblemSetEditDialog
        isOpen={editDialogType === 'edit'}
        problemSet={editedProblemSet}
        archiveSlug={archiveSlug}
        onCloseDialog={closeDialog}
      />
      <ProblemSetProblemEditDialog
        isOpen={editDialogType === 'problems'}
        problemSet={editedProblemSet}
        onCloseDialog={closeDialog}
      />
      {renderProblemSets()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
