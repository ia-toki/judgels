import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { LessonStatementCard } from '../../../../../../../../../components/LessonStatementCard/LessonStatementCard';
import { AppState } from '../../../../../../../../../modules/store';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterLessonStatement } from '../../../../../../../../../modules/api/jerahmeel/chapterLesson';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import * as chapterLessonActions from '../../modules/chapterLessonActions';
import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface ChapterLessonPageProps extends RouteComponentProps<{ lessonAlias: string }> {
  chapter: CourseChapter;
  statementLanguage: string;
  onGetLessonStatement: (chapterJid: string, lessonAlias: string, language?: string) => Promise<ChapterLessonStatement>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ChapterLessonPageState {
  response?: ChapterLessonStatement;
}

export class ChapterLessonPage extends React.Component<ChapterLessonPageProps, ChapterLessonPageState> {
  state: ChapterLessonPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetLessonStatement(
      this.props.chapter.chapterJid,
      this.props.match.params.lessonAlias,
      this.props.statementLanguage
    );

    this.setState({
      response,
    });

    this.props.onPushBreadcrumb(this.props.match.url, response.lesson.alias);
  }

  async componentDidUpdate(prevProps: ChapterLessonPageProps, prevState: ChapterLessonPageState) {
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
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  private renderStatementLanguageWidget = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { defaultLanguage, languages } = response;
    const props: StatementLanguageWidgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="statement-language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    return <LessonStatementCard alias={response.lesson.alias} statement={response.statement} />;
  };
}

const mapStateToProps = (state: AppState) => ({
  chapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetLessonStatement: chapterLessonActions.getLessonStatement,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChapterLessonPage);
