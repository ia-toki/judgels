import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { ProblemSetProblemCard } from '../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { ProblemType, getProblemName } from '../../../../modules/api/sandalphon/problem';

import * as problemActions from '../modules/problemActions';

class ProblemsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const tags = this.parseTags(queries.tags);

    this.state = {
      response: undefined,
      filter: { tags },
    };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const tags = this.parseTags(queries.tags);

    if (JSON.stringify(tags) !== JSON.stringify(this.state.filter.tags)) {
      this.setState({ filter: { tags } });
    }
  }

  render() {
    return (
      <Card title="Browse problems">
        {this.renderHeader()}
        <hr />
        {this.renderProblems()}
        {this.renderPagination()}
      </Card>
    );
  }

  renderHeader = () => {
    return <ProblemSpoilerWidget />;
  };

  renderProblems = () => {
    const { response, filter } = this.state;
    if (!response || !response.data) {
      return <LoadingState />;
    }

    const { data: problems, problemsMap, problemMetadatasMap, problemDifficultiesMap, problemProgressesMap } = response;

    if (problems.page.length === 0) {
      if (filter.tags.length === 0) {
        return (
          <>
            <p>To view problems, select some filters on the left.</p>
            <p>We will refine this page in the future.</p>
          </>
        );
      }

      return (
        <p>
          <small>No problems found.</small>
        </p>
      );
    }

    return problems.page.map(problem => {
      const { problemSetSlug, problemAlias, problemJid } = problem;
      const props = {
        problemSet: { slug: problemSetSlug },
        problem: { type: ProblemType.Programming, alias: problemAlias },
        problemName: getProblemName(problemsMap[problemJid], 'en'),
        metadata: problemMetadatasMap[problemJid],
        difficulty: problemDifficultiesMap[problemJid],
        progress: problemProgressesMap[problemJid],
      };
      return <ProblemSetProblemCard key={problemJid} {...props} />;
    });
  };

  renderPagination = () => {
    const { filter } = this.state;
    return (
      <Pagination
        pageSize={ProblemsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={JSON.stringify(filter.tags)}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const data = await this.refreshProblems(nextPage);
    return data.totalCount;
  };

  refreshProblems = async page => {
    const { filter } = this.state;
    const response = await this.props.onGetProblems(filter.tags, page);
    this.setState({ response });
    return response.data;
  };

  parseTags = queryTags => {
    let tags = queryTags || [];
    if (typeof tags === 'string') {
      tags = [tags];
    }
    return tags;
  };
}

const mapDispatchToProps = {
  onGetProblems: problemActions.getProblems,
};

export default connect(undefined, mapDispatchToProps)(ProblemsPage);
