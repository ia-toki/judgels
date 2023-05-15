import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as chapterSubmissionActions from '../../modules/chapterSubmissionActions';

export class ChapterSubmissionPage extends Component {
  state = {
    submissionWithSource: undefined,
    sourceImageUrl: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerName: undefined,
  };

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerName } = await this.props.onGetSubmissionWithSource(
      +this.props.match.params.submissionId,
      this.props.statementLanguage
    );
    const sourceImageUrl = data.source ? undefined : await this.props.onGetSubmissionSourceImage(data.submission.jid);
    this.props.onPushBreadcrumb(this.props.match.url, '#' + data.submission.id);
    this.setState({
      submissionWithSource: data,
      sourceImageUrl,
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

  renderSubmission = () => {
    const { submissionWithSource, profile, problemName, problemAlias, containerName, sourceImageUrl } = this.state;
    const { course, chapter } = this.props;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        sourceImageUrl={sourceImageUrl}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}`}
        containerTitle="Chapter"
        containerName={containerName}
      />
    );
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionWithSource: chapterSubmissionActions.getSubmissionWithSource,
  onGetSubmissionSourceImage: chapterSubmissionActions.getSubmissionSourceImage,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionPage));
