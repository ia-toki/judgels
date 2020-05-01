import * as React from 'react';
import { connect } from 'react-redux';

import Pagination from '../../../../components/Pagination/Pagination';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';
import { ProblemSetEditDialog } from '../ProblemSetEditDialog/ProblemSetEditDialog';
import { ProblemSetProblemEditDialog } from '../ProblemSetProblemEditDialog/ProblemSetProblemEditDialog';
import { ProblemSetsTable } from '../ProblemSetsTable/ProblemSetsTable';
import {
  ProblemSetsResponse,
  ProblemSetCreateData,
  ProblemSet,
  ProblemSetUpdateData,
} from '../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblemsResponse, ProblemSetProblemData } from '../../../../modules/api/jerahmeel/problemSetProblem';
import * as problemSetActions from '../modules/problemSetActions';

export interface ProblemSetsPageProps {
  onGetProblemSets: (page: number) => Promise<ProblemSetsResponse>;
  onCreateProblemSet: (data: ProblemSetCreateData) => Promise<void>;
  onUpdateProblemSet: (problemSetJid: string, data: ProblemSetUpdateData) => Promise<void>;
  onGetProblems: (problemSetJid: string) => Promise<ProblemSetProblemsResponse>;
  onSetProblems: (problemSetJid: string, data: ProblemSetProblemData[]) => Promise<void>;
}

interface ProblemSetsPageState {
  response?: ProblemSetsResponse;
  isEditDialogOpen: boolean;
  isEditProblemsDialogOpen: boolean;
  editedProblemSet?: ProblemSet;
}

class ProblemSetsPage extends React.Component<ProblemSetsPageProps, ProblemSetsPageState> {
  private static PAGE_SIZE = 20;

  state: ProblemSetsPageState = {
    isEditDialogOpen: false,
    isEditProblemsDialogOpen: false,
  };

  render() {
    return (
      <ContentCard>
        <h3>Problemsets</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderEditProblemsDialog()}
        {this.renderProblemSets()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderCreateDialog = () => {
    return <ProblemSetCreateDialog onCreateProblemSet={this.createProblemSet} />;
  };

  private renderEditDialog = () => {
    const { isEditDialogOpen, editedProblemSet, response } = this.state;
    const archiveSlug = response && editedProblemSet && response.archiveSlugsMap[editedProblemSet.archiveJid];
    return (
      <ProblemSetEditDialog
        isOpen={isEditDialogOpen}
        problemSet={editedProblemSet}
        archiveSlug={archiveSlug}
        onUpdateProblemSet={this.updateProblemSet}
        onCloseDialog={() => this.editProblemSet(undefined)}
      />
    );
  };

  private renderEditProblemsDialog = () => {
    const { isEditProblemsDialogOpen, editedProblemSet } = this.state;
    return (
      <ProblemSetProblemEditDialog
        isOpen={isEditProblemsDialogOpen}
        problemSet={editedProblemSet}
        onGetProblems={this.props.onGetProblems}
        onSetProblems={this.props.onSetProblems}
        onCloseDialog={() => this.editProblemSetProblems(undefined)}
      />
    );
  };

  private renderProblemSets = () => {
    const { response } = this.state;
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
        onEditProblemSet={this.editProblemSet}
        onEditProblemSetProblems={this.editProblemSetProblems}
      />
    );
  };

  private renderPagination = () => {
    return <Pagination pageSize={ProblemSetsPage.PAGE_SIZE} onChangePage={this.onChangePage} key={1} />;
  };

  private onChangePage = async (nextPage?: number) => {
    const response = await this.props.onGetProblemSets(nextPage);
    this.setState({ response });
    return response.data.totalCount;
  };

  private createProblemSet = async (data: ProblemSetCreateData) => {
    await this.props.onCreateProblemSet(data);
    await this.onChangePage(1);
  };

  private editProblemSet = async (problemSet?: ProblemSet) => {
    this.setState({
      isEditDialogOpen: !!problemSet,
      editedProblemSet: problemSet,
    });
  };

  private updateProblemSet = async (problemSetJid: string, data: ProblemSetUpdateData) => {
    await this.props.onUpdateProblemSet(problemSetJid, data);
    this.editProblemSet(undefined);
    await this.onChangePage(1);
  };

  private editProblemSetProblems = async (problemSet?: ProblemSet) => {
    this.setState({
      isEditProblemsDialogOpen: !!problemSet,
      editedProblemSet: problemSet,
    });
  };
}

const mapDispatchToProps = {
  onGetProblemSets: problemSetActions.getProblemSets,
  onCreateProblemSet: problemSetActions.createProblemSet,
  onUpdateProblemSet: problemSetActions.updateProblemSet,
  onGetProblems: problemSetActions.getProblems,
  onSetProblems: problemSetActions.setProblems,
};
export default connect(undefined, mapDispatchToProps)(ProblemSetsPage);
