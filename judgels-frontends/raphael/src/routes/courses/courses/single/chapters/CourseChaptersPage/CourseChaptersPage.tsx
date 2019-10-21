import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { CourseChapterCard, CourseChapterCardProps } from '../CourseChapterCard/CourseChapterCard';
import { Course } from '../../../../../../modules/api/jerahmeel/course';
import { CourseChaptersResponse } from '../../../../../../modules/api/jerahmeel/courseChapter';
import { AppState } from '../../../../../../modules/store';
import { selectCourse } from '../../../modules/courseSelectors';
import { courseChapterActions as injectedCourseChapterActions } from '../modules/courseChapterActions';

export interface CourseChaptersPageProps {
  course: Course;
  onGetChapters: (courseJid: string) => Promise<CourseChaptersResponse>;
}

interface CourseChaptersPageState {
  response?: CourseChaptersResponse;
}

export class CourseChaptersPage extends React.PureComponent<CourseChaptersPageProps, CourseChaptersPageState> {
  state: CourseChaptersPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetChapters(this.props.course.jid);
    this.setState({ response });
  }

  render() {
    return (
      <ContentCard>
        <h3>Chapters</h3>
        <hr />
        {this.renderChapters()}
      </ContentCard>
    );
  }

  private renderChapters = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: chapters } = response;

    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    return chapters.map(chapter => {
      const props: CourseChapterCardProps = {
        course: this.props.course,
        chapter,
        chapterName: this.state.response!.chaptersMap[chapter.chapterJid].name,
      };
      return <CourseChapterCard key={chapter.chapterJid} {...props} />;
    });
  };
}

export function createCourseChaptersPage(courseChapterActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state)!,
  });

  const mapDispatchToProps = {
    onGetChapters: courseChapterActions.getChapters,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(CourseChaptersPage));
}

export default createCourseChaptersPage(injectedCourseChapterActions);
