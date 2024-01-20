import { ChevronRight, Home } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ChapterProblemProgressTag } from '../../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { sendGAEvent } from '../../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectCourseChapters } from '../../../../modules/courseChaptersSelectors';
import { ChapterNavigation } from '../../../resources/ChapterNavigation/ChapterNavigation';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import {
  selectChapterProblemRefreshKey,
  selectChapterProblemShouldScrollToEditorial,
} from '../modules/chapterProblemSelectors';

import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as chapterProblemActions from '../modules/chapterProblemActions';

import './ChapterProblemPage.scss';

export class ChapterProblemPage extends Component {
  state = {
    response: undefined,
  };

  componentDidMount() {
    this.refreshProblem();
  }

  async componentDidUpdate(prevProps) {
    if (
      this.props.statementLanguage !== prevProps.statementLanguage ||
      this.props.refreshKey !== prevProps.refreshKey ||
      this.props.match.params.problemAlias !== prevProps.match.params.problemAlias
    ) {
      const shouldScrollToEditorial =
        this.props.refreshKey !== prevProps.refreshKey && this.props.shouldScrollToEditorial;
      await this.refreshProblem(shouldScrollToEditorial);
    }
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.path);
  }

  render() {
    return (
      <div className="chapter-problem-page">
        {this.renderHeader()}
        <hr />
        {this.renderContent()}
      </div>
    );
  }

  refreshProblem = async shouldScrollToEditorial => {
    this.setState({
      response: undefined,
    });

    const response = await this.props.onGetProblemWorksheet(
      this.props.chapter.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState(
      {
        response,
      },
      () => {
        if (shouldScrollToEditorial && response.editorial) {
          const problemStatementEl = document.getElementById('chapter-problem-statement');
          if (problemStatementEl) {
            problemStatementEl.scrollTo({ top: problemStatementEl.scrollHeight, behavior: 'smooth' });
          }
        }
      }
    );

    this.props.onPushBreadcrumb(this.props.match.path, response.problem.alias);

    sendGAEvent({ category: 'Courses', action: 'View course problem', label: this.props.course.name });
    sendGAEvent({ category: 'Courses', action: 'View chapter problem', label: this.props.chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'View problem',
      label: this.props.chapterName + ': ' + this.props.match.params.problemAlias,
    });
  };

  renderHeader = () => {
    const { course, chapter, match } = this.props;
    const { response } = this.state;
    const problemTitle = response && response.worksheet.statement.title;

    return (
      <div className="chapter-problem-page__title">
        <h3>
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}`}>
            <Home />
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}/chapters/${chapter.alias}`}>
            {chapter.alias}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          {match.params.problemAlias}. {problemTitle}
        </h3>

        {this.renderProgress()}
        {this.renderNavigation()}
      </div>
    );
  };

  renderProgress = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { progress } = response;
    if (!progress) {
      return null;
    }

    return <ChapterProblemProgressTag verdict={progress.verdict} />;
  };

  renderNavigation = () => {
    const { course, chapter, chapters } = this.props;
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { previousResourcePath, nextResourcePath } = response;
    return (
      <ChapterNavigation
        courseSlug={course.slug}
        chapterAlias={chapter.alias}
        previousResourcePath={previousResourcePath}
        nextResourcePath={nextResourcePath}
        chapters={chapters}
      />
    );
  };

  renderContent = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ChapterProblemProgrammingPage worksheet={response} renderNavigation={this.renderNavigation} />;
    } else {
      return <ChapterProblemBundlePage worksheet={response} />;
    }
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  chapters: selectCourseChapters(state),
  refreshKey: selectChapterProblemRefreshKey(state),
  shouldScrollToEditorial: selectChapterProblemShouldScrollToEditorial(state),
  statementLanguage: selectStatementLanguage(state),
});
const mapDispatchToProps = {
  onGetProblemWorksheet: chapterProblemActions.getProblemWorksheet,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
