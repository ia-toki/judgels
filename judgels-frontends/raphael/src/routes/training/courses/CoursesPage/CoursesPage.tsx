import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';
import { CourseEditDialog } from '../CourseEditDialog/CourseEditDialog';
import { CoursesTable } from '../CoursesTable/CoursesTable';
import { UserRole } from '../../../../modules/api/jophiel/role';
import { CoursesResponse, CourseCreateData, Course, CourseUpdateData } from '../../../../modules/api/jerahmeel/course';
import * as courseActions from '../modules/courseActions';

export interface CoursePageProps {
  role: UserRole;
  onGetCourses: () => Promise<CoursesResponse>;
  onCreateCourse: (data: CourseCreateData) => Promise<void>;
  onUpdateCourse: (courseJid: string, data: CourseUpdateData) => Promise<void>;
}

export interface CoursesPageState {
  response?: CoursesResponse;
  isEditDialogOpen: boolean;
  editedCourse?: Course;
}

class CoursesPage extends React.Component<CoursePageProps, CoursesPageState> {
  state: CoursesPageState = {
    isEditDialogOpen: false,
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
        {this.renderCourses()}
      </ContentCard>
    );
  }

  private refreshCourses = async () => {
    const response = await this.props.onGetCourses();
    this.setState({ response });
  };

  private renderCreateDialog = () => {
    return <CourseCreateDialog onCreateCourse={this.createCourse} />;
  };

  private renderEditDialog = () => {
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

  private renderCourses = () => {
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

    return <CoursesTable courses={courses} onEditCourse={this.editCourse} />;
  };

  private createCourse = async (data: CourseCreateData) => {
    await this.props.onCreateCourse(data);
    await this.refreshCourses();
  };

  private editCourse = async (course?: Course) => {
    this.setState({
      isEditDialogOpen: !!course,
      editedCourse: course,
    });
  };

  private updateCourse = async (courseJid: string, data: CourseUpdateData) => {
    await this.props.onUpdateCourse(courseJid, data);
    this.editCourse(undefined);
    await this.refreshCourses();
  };
}

const mapDispatchToProps = {
  onGetCourses: courseActions.getCourses,
  onCreateCourse: courseActions.createCourse,
  onUpdateCourse: courseActions.updateCourse,
};
export default connect(undefined, mapDispatchToProps)(CoursesPage);
