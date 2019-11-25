import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { AppState } from '../../../../../../../../../modules/store';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { Course } from '../../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import {
  SubmissionWithSource,
  SubmissionWithSourceResponse,
} from '../../../../../../../../../modules/api/sandalphon/submissionProgramming';
import { Profile } from '../../../../../../../../../modules/api/jophiel/profile';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import { chapterSubmissionActions as injectedChapterSubmissionActions } from '../../modules/chapterSubmissionActions';

export interface ChapterSubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  course: Course;
  chapter: CourseChapter;
  statementLanguage: string;
  onGetSubmissionWithSource: (
    chapterJid: string,
    submissionId: number,
    language?: string
  ) => Promise<SubmissionWithSourceResponse>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ChapterSubmissionPageState {
  submissionWithSource?: SubmissionWithSource;
  profile?: Profile;
  problemName?: string;
  problemAlias?: string;
  containerName?: string;
}

export class ChapterSubmissionPage extends React.Component<ChapterSubmissionPageProps, ChapterSubmissionPageState> {
  state: ChapterSubmissionPageState = {};

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerName } = await this.props.onGetSubmissionWithSource(
      this.props.chapter.chapterJid,
      +this.props.match.params.submissionId,
      this.props.statementLanguage
    );
    this.props.onPushBreadcrumb(this.props.match.url, 'Submission #' + data.submission.id);
    this.setState({
      submissionWithSource: data,
      profile,
      problemName,
      problemAlias,
      containerName,
    });
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return (
      <ContentCard>
        <h3>Submission #{this.props.match.params.submissionId}</h3>
        <hr />
        {this.renderSubmission()}
      </ContentCard>
    );
  }

  private renderSubmission = () => {
    const { submissionWithSource, profile, problemName, problemAlias, containerName } = this.state;
    const { course, chapter } = this.props;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile!}
        problemName={problemName!}
        problemAlias={problemAlias!}
        problemUrl={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}`}
        containerTitle="Chapter"
        containerName={containerName!}
      />
    );
  };
}

function createChapterSubmissionPage(chapterProgrammingSubmissionActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
    chapter: selectCourseChapter(state),
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionWithSource: chapterProgrammingSubmissionActions.getSubmissionWithSource,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionPage));
}

export default createChapterSubmissionPage(injectedChapterSubmissionActions, injectedBreadcrumbsActions);
