import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { sendGAEvent } from '../../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import * as chapterProblemActions from '../../modules/chapterProblemActions';
import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

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
  }
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
