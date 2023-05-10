import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';
import { CourseEditDialog } from '../CourseEditDialog/CourseEditDialog';
import { CoursesTable } from '../CoursesTable/CoursesTable';
import { CourseChapterEditDialog } from '../CourseChapterEditDialog/CourseChapterEditDialog';
import * as courseActions from '../modules/courseActions';

class CoursesPage extends Component {
  state = {
    response: undefined,
    isEditDialogOpen: false,
    isEditChaptersDialogOpen: false,
    editedCourse: undefined,
  };

  componentDidMount() {
    this.refreshCourses();
  }

  render() {
    return (
      <ContentCard>
        <h3>Courses</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderEditChaptersDialog()}
        {this.renderCourses()}
      </ContentCard>
    );
  }

  refreshCourses = async () => {
    const response = await this.props.onGetCourses();
    this.setState({ response });
  };

  renderCreateDialog = () => {
    return <CourseCreateDialog onCreateCourse={this.createCourse} />;
  };

  renderEditDialog = () => {
    const { isEditDialogOpen, editedCourse } = this.state;
    return (
      <CourseEditDialog
        isOpen={isEditDialogOpen}
        course={editedCourse}
        onUpdateCourse={this.updateCourse}
        onCloseDialog={() => this.editCourse(undefined)}
      />
    );
  };

  renderEditChaptersDialog = () => {
    const { isEditChaptersDialogOpen, editedCourse } = this.state;
    return (
      <CourseChapterEditDialog
        isOpen={isEditChaptersDialogOpen}
        course={editedCourse}
        onGetChapters={this.props.onGetChapters}
        onSetChapters={this.props.onSetChapters}
        onCloseDialog={() => this.editCourseChapters(undefined)}
      />
    );
  };

  renderCourses = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courses } = response;
    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return (
      <CoursesTable courses={courses} onEditCourse={this.editCourse} onEditCourseChapters={this.editCourseChapters} />
    );
  };

  createCourse = async data => {
    await this.props.onCreateCourse(data);
    await this.refreshCourses();
  };

  editCourse = async course => {
    this.setState({
      isEditDialogOpen: !!course,
      editedCourse: course,
    });
  };

  updateCourse = async (courseJid, data) => {
    await this.props.onUpdateCourse(courseJid, data);
    this.editCourse(undefined);
    await this.refreshCourses();
  };

  editCourseChapters = async course => {
    this.setState({
      isEditChaptersDialogOpen: !!course,
      editedCourse: course,
    });
  };
}

const mapDispatchToProps = {
  onGetCourses: courseActions.getCourses,
  onCreateCourse: courseActions.createCourse,
  onUpdateCourse: courseActions.updateCourse,
  onGetChapters: courseActions.getChapters,
  onSetChapters: courseActions.setChapters,
};
export default connect(undefined, mapDispatchToProps)(CoursesPage);
