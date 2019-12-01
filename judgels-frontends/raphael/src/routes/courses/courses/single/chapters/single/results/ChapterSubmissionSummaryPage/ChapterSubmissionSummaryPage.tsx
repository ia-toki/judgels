import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../modules/store';
import { Profile } from '../../../../../../../../modules/api/jophiel/profile';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { SubmissionSummaryResponse } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { SubmissionConfig } from '../../../../../../../../modules/api/jerahmeel/submission';
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
  chapter: CourseChapter;
  language?: string;
  onGetSubmissionSummary: (
    chapterJid: string,
    username?: string,
    language?: string
  ) => Promise<SubmissionSummaryResponse>;
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
    const { chapter, onGetSubmissionSummary } = this.props;
    const response = await onGetSubmissionSummary(
      chapter.chapterJid,
      this.props.match.params.username,
      this.props.language
    );

    const problemSummaries: ProblemSubmissionCardProps[] = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canManage: true,
      itemTypesMap: response.itemTypesMap,
    }));

    this.setState({ config: response.config, problemSummaries });
  }

  async componentDidMount() {
    await this.refreshSubmissions();
  }

  render() {
    return (
      <ContentCard>
        <h3>Quiz Results</h3>
        <hr />
        {this.renderResults()}
      </ContentCard>
    );
  }

  private renderResults = () => {
    const { problemSummaries } = this.state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No quizzes.</small>;
    }
    return this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />);
  };
}

export function createChapterSubmissionSummaryPage(chapterSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    chapter: selectCourseChapter(state),
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionSummary: chapterSubmissionActions.getSubmissionSummary,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionSummaryPage));
}

export default createChapterSubmissionSummaryPage(injectedChapterSubmissionActions);
