import * as React from 'react';
import { connect } from 'react-redux';

import Pagination from '../../../../components/Pagination/Pagination';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';
import { ProblemSetEditDialog } from '../ProblemSetEditDialog/ProblemSetEditDialog';
import { ProblemSetsTable } from '../ProblemSetsTable/ProblemSetsTable';
import {
  ProblemSetsResponse,
  ProblemSetCreateData,
  ProblemSet,
  ProblemSetUpdateData,
} from '../../../../modules/api/jerahmeel/problemSet';
import * as problemSetActions from '../modules/problemSetActions';

export interface ProblemSetsPageProps {
  onGetProblemSets: (page: number) => Promise<ProblemSetsResponse>;
  onCreateProblemSet: (data: ProblemSetCreateData) => Promise<void>;
  onUpdateProblemSet: (problemSetJid: string, data: ProblemSetUpdateData) => Promise<void>;
}

interface ProblemSetsPageState {
  response?: ProblemSetsResponse;
  isEditDialogOpen: boolean;
  editedProblemSet?: ProblemSet;
}

class ProblemSetsPage extends React.Component<ProblemSetsPageProps, ProblemSetsPageState> {
  private static PAGE_SIZE = 20;

  state: ProblemSetsPageState = {
    isEditDialogOpen: false,
  };

  render() {
    return (
      <ContentCard>
        <h3>Problemsets</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
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
        onEditProblemSetProblems={() => {}}
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
}

const mapDispatchToProps = {
  onGetProblemSets: problemSetActions.getProblemSets,
  onCreateProblemSet: problemSetActions.createProblemSet,
  onUpdateProblemSet: problemSetActions.updateProblemSet,
};
export default connect(undefined, mapDispatchToProps)(ProblemSetsPage);
