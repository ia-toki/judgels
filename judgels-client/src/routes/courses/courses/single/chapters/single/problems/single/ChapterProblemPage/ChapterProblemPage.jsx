import { ChevronRight } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { sendGAEvent } from '../../../../../../../../../ga';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ChapterProblemProgressTag } from '../../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import * as chapterProblemActions from '../../modules/chapterProblemActions';
import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

import './ChapterProblemPage.scss';

export class ChapterProblemPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblemWorksheet(
      this.props.chapter.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState({
      response,
    });

    this.props.onPushBreadcrumb(this.props.match.url, response.problem.alias);

    sendGAEvent({ category: 'Courses', action: 'View course problem', label: this.props.course.name });
    sendGAEvent({ category: 'Courses', action: 'View chapter problem', label: this.props.chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'View problem',
      label: this.props.chapterName + ': ' + this.props.match.params.problemAlias,
    });
  }

  async componentDidUpdate(prevProps, prevState) {
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
      <div className="chapter-problem-page">
        {this.renderHeader()}
        <hr />
        {this.renderContent()}
      </div>
    );
  }

  renderHeader = () => {
    const { course, chapter, match } = this.props;

    return (
      <div className="chapter-problem-page__title">
        <h3>
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}`}>
            {course.name}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}/chapters/${chapter.alias}`}>
            {chapter.alias}. {chapter.name}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          {match.params.problemAlias}
        </h3>

        {this.renderProgress()}
      </div>
    );
  };

  renderProgress = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { progress } = response;

    return <ChapterProblemProgressTag verdict={progress.verdict} />;
  };

  renderContent = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ChapterProblemProgrammingPage worksheet={response} />;
    } else {
      return <ChapterProblemBundlePage worksheet={response} />;
    }
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  statementLanguage: selectStatementLanguage(state),
});
const mapDispatchToProps = {
  onGetProblemWorksheet: chapterProblemActions.getProblemWorksheet,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
