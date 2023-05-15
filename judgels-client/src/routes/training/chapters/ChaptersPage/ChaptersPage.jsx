import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ChapterCreateDialog } from '../ChapterCreateDialog/ChapterCreateDialog';
import { ChapterEditDialog } from '../ChapterEditDialog/ChapterEditDialog';
import { ChapterLessonEditDialog } from '../ChapterLessonEditDialog/ChapterLessonEditDialog';
import { ChapterProblemEditDialog } from '../ChapterProblemEditDialog/ChapterProblemEditDialog';
import { ChaptersTable } from '../ChaptersTable/ChaptersTable';
import * as chapterActions from '../modules/chapterActions';

class ChaptersPage extends Component {
  state = {
    response: undefined,
    isEditDialogOpen: false,
    isEditLessonsDialogOpen: false,
    isEditProblemsDialogOpen: false,
    editedChapter: undefined,
  };

  componentDidMount() {
    this.refreshChapters();
  }

  render() {
    return (
      <ContentCard>
        <h3>Chapters</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderEditLessonsDialog()}
        {this.renderEditProblemsDialog()}
        {this.renderChapters()}
      </ContentCard>
    );
  }

  refreshChapters = async () => {
    const response = await this.props.onGetChapters();
    this.setState({ response });
  };

  renderCreateDialog = () => {
    return <ChapterCreateDialog onCreateChapter={this.createChapter} />;
  };

  renderEditDialog = () => {
    const { isEditDialogOpen, editedChapter } = this.state;
    return (
      <ChapterEditDialog
        isOpen={isEditDialogOpen}
        chapter={editedChapter}
        onUpdateChapter={this.updateChapter}
        onCloseDialog={() => this.editChapter(undefined)}
      />
    );
  };

  renderEditLessonsDialog = () => {
    const { isEditLessonsDialogOpen, editedChapter } = this.state;
    return (
      <ChapterLessonEditDialog
        isOpen={isEditLessonsDialogOpen}
        chapter={editedChapter}
        onGetLessons={this.props.onGetLessons}
        onSetLessons={this.props.onSetLessons}
        onCloseDialog={() => this.editChapterLessons(undefined)}
      />
    );
  };

  renderEditProblemsDialog = () => {
    const { isEditProblemsDialogOpen, editedChapter } = this.state;
    return (
      <ChapterProblemEditDialog
        isOpen={isEditProblemsDialogOpen}
        chapter={editedChapter}
        onGetProblems={this.props.onGetProblems}
        onSetProblems={this.props.onSetProblems}
        onCloseDialog={() => this.editChapterProblems(undefined)}
      />
    );
  };

  renderChapters = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: chapters } = response;
    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    return (
      <ChaptersTable
        chapters={chapters}
        onEditChapter={this.editChapter}
        onEditChapterLessons={this.editChapterLessons}
        onEditChapterProblems={this.editChapterProblems}
      />
    );
  };

  createChapter = async data => {
    await this.props.onCreateChapter(data);
    await this.refreshChapters();
  };

  editChapter = async chapter => {
    this.setState({
      isEditDialogOpen: !!chapter,
      editedChapter: chapter,
    });
  };

  updateChapter = async (chapterJid, data) => {
    await this.props.onUpdateChapter(chapterJid, data);
    this.editChapter(undefined);
    await this.refreshChapters();
  };

  editChapterLessons = async chapter => {
    this.setState({
      isEditLessonsDialogOpen: !!chapter,
      editedChapter: chapter,
    });
  };

  editChapterProblems = async chapter => {
    this.setState({
      isEditProblemsDialogOpen: !!chapter,
      editedChapter: chapter,
    });
  };
}

const mapDispatchToProps = {
  onGetChapters: chapterActions.getChapters,
  onCreateChapter: chapterActions.createChapter,
  onUpdateChapter: chapterActions.updateChapter,
  onGetLessons: chapterActions.getLessons,
  onSetLessons: chapterActions.setLessons,
  onGetProblems: chapterActions.getProblems,
  onSetProblems: chapterActions.setProblems,
};
export default connect(undefined, mapDispatchToProps)(ChaptersPage);
