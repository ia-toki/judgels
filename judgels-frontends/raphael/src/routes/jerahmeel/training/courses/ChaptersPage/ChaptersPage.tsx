// import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from 'components/Card/Card';
import { ChaptersResponse } from 'modules/api/jerahmeel/chapter';
import { Course } from 'modules/api/jerahmeel/course';

import { chapterActions as injectedChapterActions } from '../modules/chapterActions';
import { courseActions as injectedCourseActions } from '../modules/courseActions';
import { ChapterCard } from '../ChapterCard/ChapterCard';
import { LoadingChapterCard } from '../ChapterCard/LoadingChapterCard';

export interface ChaptersPageProps extends RouteComponentProps<{ courseId: number }> {
  onGetCourseById: (courseId: number) => Promise<Course>;
  onGetChapters: (courseId: number) => Promise<ChaptersResponse>;
}

export interface ChaptersPageState {
  course?: Course;
  response?: Partial<ChaptersResponse>;
}

class ChaptersPage extends React.Component<ChaptersPageProps, ChaptersPageState> {
  state: ChaptersPageState = {};

  async componentDidMount() {
    const { courseId } = this.props.match.params;
    const [course, response] = await Promise.all([
      this.props.onGetCourseById(courseId),
      this.props.onGetChapters(courseId),
    ]);
    this.setState({ course, response });
  }

  render() {
    const { course } = this.state;
    const courseTitle = course ? `Course ${course.id}: ${course.name}` : 'Course';
    return <Card title={courseTitle}>{this.renderChapters()}</Card>;
  }

  private renderChapters = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingChapterCard />;
    }

    const chapters = response.data;
    if (!chapters) {
      return <LoadingChapterCard />;
    }

    return chapters.page.map(chapter => (
      <ChapterCard key={chapter.jid} course={this.state.course!} chapter={chapter} />
    ));
  };
}

export function createChaptersPage(chapterActions, courseActions) {
  const mapDispatchToProps = {
    onGetChapters: chapterActions.getChapters,
    onGetCourseById: courseActions.getCourseById,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ChaptersPage));
}

export default createChaptersPage(injectedChapterActions, injectedCourseActions);
