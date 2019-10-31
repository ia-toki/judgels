import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { AppState } from '../../../../../../../../../modules/store';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import {
  selectStatementLanguage,
  selectGradingLanguage,
} from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { chapterProblemActions as injectedChapterProblemActions } from '../../modules/chapterProblemActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  chapter: CourseChapter;
  statementLanguage: string;
  gradingLanguage: string;
  onGetProblemWorksheet: (
    chapterJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ChapterProblemWorksheet>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ChapterProblemPageState {
  response?: ChapterProblemWorksheet;
}

export class ChapterProblemPage extends React.Component<ChapterProblemPageProps, ChapterProblemPageState> {
  state: ChapterProblemPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetProblemWorksheet(
      this.props.chapter.chapterJid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState({
      response,
    });

    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + response.problem.alias);
  }

  async componentDidUpdate(prevProps: ChapterProblemPageProps, prevState: ChapterProblemPageState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.response) {
      this.setState({ response: undefined });
    } else if (!this.state.response && prevState.response) {
      await this.componentDidMount();
    }
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }
    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ChapterProblemProgrammingPage worksheet={response} gradingLanguage={this.props.gradingLanguage} />;
    } else {
      return <ChapterProblemBundlePage worksheet={response} />;
    }
  }
}

export function createChapterProblemPage(chapterProblemActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    chapter: selectCourseChapter(state).courseChapter,
    statementLanguage: selectStatementLanguage(state),
    gradingLanguage: selectGradingLanguage(state),
  });
  const mapDispatchToProps = {
    onGetProblemWorksheet: chapterProblemActions.getProblemWorksheet,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
}

export default createChapterProblemPage(injectedChapterProblemActions, injectedBreadcrumbsActions);
