import { useState } from 'react';
import { useDispatch } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';
import { ProblemSetEditDialog } from '../ProblemSetEditDialog/ProblemSetEditDialog';
import { ProblemSetProblemEditDialog } from '../ProblemSetProblemEditDialog/ProblemSetProblemEditDialog';
import { ProblemSetsTable } from '../ProblemSetsTable/ProblemSetsTable';

import * as problemSetActions from '../modules/problemSetActions';

const PAGE_SIZE = 20;

export default function ProblemSetsPage() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
    isEditDialogOpen: false,
    isEditProblemsDialogOpen: false,
    editedProblemSet: undefined,
  });

  const render = () => {
    return (
      <ContentCard>
        <h3>Problemsets</h3>
        <hr />
        {renderCreateDialog()}
        {renderEditDialog()}
        {renderEditProblemsDialog()}
        {renderProblemSets()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderCreateDialog = () => {
    return <ProblemSetCreateDialog onCreateProblemSet={createProblemSet} />;
  };

  const renderEditDialog = () => {
    const { isEditDialogOpen, editedProblemSet, response } = state;
    const archiveSlug = response && editedProblemSet && response.archiveSlugsMap[editedProblemSet.archiveJid];
    return (
      <ProblemSetEditDialog
        isOpen={isEditDialogOpen}
        problemSet={editedProblemSet}
        archiveSlug={archiveSlug}
        onUpdateProblemSet={updateProblemSet}
        onCloseDialog={() => editProblemSet(undefined)}
      />
    );
  };

  const getProblems = problemSetJid => dispatch(problemSetActions.getProblems(problemSetJid));
  const setProblems = (problemSetJid, data) => dispatch(problemSetActions.setProblems(problemSetJid, data));

  const renderEditProblemsDialog = () => {
    const { isEditProblemsDialogOpen, editedProblemSet } = state;
    return (
      <ProblemSetProblemEditDialog
        isOpen={isEditProblemsDialogOpen}
        problemSet={editedProblemSet}
        onGetProblems={getProblems}
        onSetProblems={setProblems}
        onCloseDialog={() => editProblemSetProblems(undefined)}
      />
    );
  };

  const renderProblemSets = () => {
    const { response } = state;
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

  const renderPagination = () => {
    return <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} key={1} />;
  };

  const onChangePage = async nextPage => {
    const response = await dispatch(problemSetActions.getProblemSets(nextPage));
    setState(prevState => ({ ...prevState, response }));
    return response.data.totalCount;
  };

  const createProblemSet = async data => {
    await dispatch(problemSetActions.createProblemSet(data));
  };

  const editProblemSet = problemSet => {
    setState(prevState => ({
      ...prevState,
      isEditDialogOpen: !!problemSet,
      editedProblemSet: problemSet,
    }));
  };

  const updateProblemSet = async (problemSetJid, data) => {
    await dispatch(problemSetActions.updateProblemSet(problemSetJid, data));
    editProblemSet(undefined);
  };

  const editProblemSetProblems = problemSet => {
    setState(prevState => ({
      ...prevState,
      isEditProblemsDialogOpen: !!problemSet,
      editedProblemSet: problemSet,
    }));
  };

  return render();
}
