import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';
import { ProblemSetEditDialog } from '../ProblemSetEditDialog/ProblemSetEditDialog';
import { ProblemSetProblemEditDialog } from '../ProblemSetProblemEditDialog/ProblemSetProblemEditDialog';
import { ProblemSetsTable } from '../ProblemSetsTable/ProblemSetsTable';

import * as problemSetActions from '../modules/problemSetActions';

class ProblemSetsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const page = queries.page;

    this.state = {
      page,
      response: undefined,
      isEditDialogOpen: false,
      isEditProblemsDialogOpen: false,
      editedProblemSet: undefined,
    };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const page = queries.page;

    if (page !== this.state.page) {
      this.setState({ page });
    }
  }

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

  renderCreateDialog = () => {
    return <ProblemSetCreateDialog onCreateProblemSet={this.createProblemSet} />;
  };

  renderEditDialog = () => {
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

  renderEditProblemsDialog = () => {
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

  renderProblemSets = () => {
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

  renderPagination = () => {
    return <Pagination pageSize={ProblemSetsPage.PAGE_SIZE} onChangePage={this.onChangePage} key={1} />;
  };

  onChangePage = async nextPage => {
    const response = await this.props.onGetProblemSets(nextPage);
    this.setState({ response });
    return response.data.totalCount;
  };

  createProblemSet = async data => {
    await this.props.onCreateProblemSet(data);
    await this.onChangePage(1);
  };

  editProblemSet = async problemSet => {
    this.setState({
      isEditDialogOpen: !!problemSet,
      editedProblemSet: problemSet,
    });
  };

  updateProblemSet = async (problemSetJid, data) => {
    await this.props.onUpdateProblemSet(problemSetJid, data);
    this.editProblemSet(undefined);
    await this.onChangePage(this.state.page);
  };

  editProblemSetProblems = async problemSet => {
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
