import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { AppState } from '../../../../../../../../modules/store';
import { Profile } from '../../../../../../../../modules/api/jophiel/profile';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { SubmissionSummaryResponse } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { SubmissionConfig } from '../../../../../../../../modules/api/jerahmeel/submission';
import { selectMaybeUserJid } from '../../../../../../../../modules/session/sessionSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import {
  ProblemSubmissionCard,
  ProblemSubmissionCardProps,
} from '../../../../../../../../components/SubmissionDetails/Bundle/ProblemSubmissionsCard/ProblemSubmissionCard';
import { chapterSubmissionActions as injectedChapterSubmissionActions } from '../modules/chapterSubmissionActions';

interface ChapterSubmissionSummaryPageRoute {
  username?: string;
}

export interface ChapterSubmissionSummaryPageProps extends RouteComponentProps<ChapterSubmissionSummaryPageRoute> {
  userJid?: string;
  chapter: CourseChapter;
  language?: string;
  onGetSubmissionSummary: (
    chapterJid: string,
    username?: string,
    language?: string
  ) => Promise<SubmissionSummaryResponse>;
  onRegradeAll: (chapterJid: string, userJid?: string, problemJid?: string) => Promise<void>;
}

export interface ChapterSubmissionSummaryPageState {
  config?: SubmissionConfig;
  profile?: Profile;
  problemSummaries: ProblemSubmissionCardProps[];
}

class ChapterSubmissionSummaryPage extends React.Component<
  ChapterSubmissionSummaryPageProps,
  ChapterSubmissionSummaryPageState
> {
  state: ChapterSubmissionSummaryPageState = {
    config: undefined,
    problemSummaries: undefined,
  };

  async refreshSubmissions() {
    const { userJid, chapter, onGetSubmissionSummary } = this.props;
    if (!userJid) {
      this.setState({ problemSummaries: [] });
      return;
    }

    const response = await onGetSubmissionSummary(
      chapter.chapterJid,
      this.props.match.params.username,
      this.props.language
    );

    const problemSummaries: ProblemSubmissionCardProps[] = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[chapter.chapterJid + '-' + problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
      canManage: response.config.canManage,
      itemTypesMap: response.itemTypesMap,
      onRegrade: () => this.regrade(problemJid),
    }));

    this.setState({ config: response.config, profile: response.profile, problemSummaries });
  }

  async componentDidMount() {
    await this.refreshSubmissions();
  }

  render() {
    return (
      <ContentCard>
        <h3>Quiz Results</h3>
        <hr />
        {this.renderUserFilter()}
        {this.renderResults()}
      </ContentCard>
    );
  }

  private renderUserFilter = () => {
    if (this.props.location.pathname.includes('/users/')) {
      return null;
    }
    return <ItemSubmissionUserFilter />;
  };

  private renderResults = () => {
    const { problemSummaries } = this.state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No quizzes.</small>;
    }
    return (
      <>
        <ContentCard>
          Summary for <UserRef profile={this.state.profile} />
        </ContentCard>
        {this.state.problemSummaries.map(props => (
          <ProblemSubmissionCard key={props.alias} {...props} />
        ))}
      </>
    );
  };

  private regrade = async problemJid => {
    const { userJids } = this.state.config;
    const userJid = userJids[0];

    await this.props.onRegradeAll(this.props.chapter.chapterJid, userJid, problemJid);
    await this.refreshSubmissions();
  };
}

export function createChapterSubmissionSummaryPage(chapterSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
    chapter: selectCourseChapter(state),
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionSummary: chapterSubmissionActions.getSubmissionSummary,
    onRegradeAll: chapterSubmissionActions.regradeSubmissions,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionSummaryPage));
}

export default createChapterSubmissionSummaryPage(injectedChapterSubmissionActions);
