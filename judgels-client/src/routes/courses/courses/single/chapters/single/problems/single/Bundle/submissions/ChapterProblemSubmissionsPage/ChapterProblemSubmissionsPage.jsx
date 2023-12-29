import { Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ButtonLink } from '../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import ItemSubmissionUserFilter from '../../../../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../../../../../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { SubmissionDetails } from '../../../../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { selectMaybeUserJid } from '../../../../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../modules/courseChapterSelectors';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

import './ChapterProblemSubmissionsPage.scss';

class ChapterProblemSubmissionsPage extends Component {
  state = {
    config: undefined,
    profile: undefined,
    problemSummaries: undefined,
  };

  async refreshSubmissions() {
    const { userJid, chapter, match, language, onGetSubmissionSummary } = this.props;
    if (!userJid) {
      this.setState({ problemSummaries: [] });
      return;
    }

    const response = await onGetSubmissionSummary(chapter.jid, match.params.problemAlias, language);

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[chapter.jid + '-' + problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
      canManage: false,
      itemTypesMap: response.itemTypesMap,
    }));

    this.setState({ config: response.config, profile: response.profile, problemSummaries });
  }

  async componentDidMount() {
    await this.refreshSubmissions();
  }

  render() {
    const { course, chapter, match } = this.props;

    return (
      <ContentCard className="chapter-bundle-problem-submissions-page">
        <ScrollToTopOnMount />
        <h3 className="heading-with-button-action">Results</h3>
        <ButtonLink
          intent={Intent.PRIMARY}
          to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${match.params.problemAlias}`}
        >
          Retake
        </ButtonLink>
        <hr />
        {this.renderResults()}
      </ContentCard>
    );
  }

  renderUserFilter = () => {
    if (this.props.location.pathname.includes('/users/')) {
      return null;
    }
    return <ItemSubmissionUserFilter />;
  };

  renderResults = () => {
    const { problemSummaries } = this.state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No quizzes.</small>;
    }
    return (
      <>
        {this.state.problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} />
        ))}
      </>
    );
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  language: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionSummary: chapterProblemSubmissionActions.getSubmissionSummary,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemSubmissionsPage));
