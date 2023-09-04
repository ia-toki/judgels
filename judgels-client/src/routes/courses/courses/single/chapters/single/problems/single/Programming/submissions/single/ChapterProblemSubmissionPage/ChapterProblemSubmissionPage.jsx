import { ChevronLeft } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { ButtonLink } from '../../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { SubmissionDetails } from '../../../../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../../modules/courseChapterSelectors';
import * as breadcrumbsActions from '../../../../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

export class ChapterProblemSubmissionPage extends Component {
  state = {
    submissionWithSource: undefined,
    sourceImageUrl: undefined,
    profile: undefined,
    problemName: undefined,
    containerName: undefined,
  };

  async componentDidMount() {
    const { data, profile, problemName, containerName } = await this.props.onGetSubmissionWithSource(
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
      containerName,
    });
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    const { course, chapter } = this.props;
    const { problemAlias } = this.props.match.params;

    return (
      <ContentCard>
        <h3 className="heading-with-button-action">Submission #{this.props.match.params.submissionId}</h3>
        <ButtonLink
          small
          icon={<ChevronLeft />}
          to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}/submissions`}
        >
          Back
        </ButtonLink>
        <hr />

        {this.renderSubmission()}
      </ContentCard>
    );
  }

  renderSubmission = () => {
    const { submissionWithSource, profile, sourceImageUrl } = this.state;
    const { course, chapter } = this.props;
    const { problemAlias } = this.props.match.params;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        sourceImageUrl={sourceImageUrl}
        profile={profile}
        problemUrl={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}`}
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
  onGetSubmissionWithSource: chapterProblemSubmissionActions.getSubmissionWithSource,
  onGetSubmissionSourceImage: chapterProblemSubmissionActions.getSubmissionSourceImage,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemSubmissionPage));
