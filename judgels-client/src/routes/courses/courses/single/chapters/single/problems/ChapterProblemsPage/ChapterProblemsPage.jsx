import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ChapterProblemCard } from '../ChapterProblemCard/ChapterProblemCard';
import { consolidateLanguages } from '../../../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../../../modules/api/sandalphon/problem';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import * as chapterProblemActions from '../modules/chapterProblemActions';

export class ChapterProblemsPage extends Component {
  state = {
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.chapter.jid);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.problemsMap,
      this.props.statementLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps) {
    const { response } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && response) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
        response.problemsMap,
        this.props.statementLanguage
      );

      this.setState({
        defaultLanguage,
        uniqueLanguages,
      });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {this.renderStatementLanguageWidget()}
        {this.renderProblems()}
      </ContentCard>
    );
  }

  renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  renderProblems = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemsMap, problemProgressesMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const props = {
        course: this.props.course,
        chapter: this.props.chapter,
        problem,
        problemName: getProblemName(problemsMap[problem.problemJid], this.state.defaultLanguage),
        progress: problemProgressesMap[problem.problemJid],
      };
      return <ChapterProblemCard key={problem.problemJid} {...props} />;
    });
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetProblems: chapterProblemActions.getProblems,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChapterProblemsPage);
