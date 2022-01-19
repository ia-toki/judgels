import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { SubmissionFilterWidget } from '../../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { ChapterSubmissionsTable } from '../ChapterSubmissionsTable/ChapterSubmissionsTable';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import * as chapterSubmissionActions from '../modules/chapterSubmissionActions';

export class ChapterSubmissionsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias;

    this.state = {
      response: undefined,
      filter: { problemAlias },
      isFilterLoading: false,
    };
  }

  async componentDidMount() {
    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias;

    if (problemAlias) {
      await this.refreshSubmissions();
    }

    this.setState({ filter: { problemAlias } });
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias;

    if (problemAlias !== this.state.filter.problemAlias) {
      this.setState({ filter: { problemAlias }, isFilterLoading: true });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderUserFilter()}
        {this.renderHeader()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderHeader = () => {
    return (
      <div className="content-card__header">
        <div className="action-buttons float-left">{this.renderRegradeAllButton()}</div>
        {this.renderFilterWidget()}
        <div className="clearfix" />
      </div>
    );
  };

  renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} />;
  };

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ChapterSubmissionsTable
        course={this.props.course}
        chapter={this.props.chapter}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  renderPagination = () => {
    const { filter } = this.state;

    const key = '' + filter.problemAlias + this.isUserFilterMine();
    return <Pagination key={key} pageSize={ChapterSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  onChangePage = async nextPage => {
    const { problemAlias } = this.state.filter;
    const data = await this.refreshSubmissions(problemAlias, nextPage);
    return data.totalCount;
  };

  refreshSubmissions = async (problemAlias, page) => {
    const username = this.isUserFilterMine() ? this.props.username : undefined;
    const response = await this.props.onGetProgrammingSubmissions(this.props.chapter.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response || !filter) {
      return null;
    }
    const { config, problemAliasesMap } = response;
    const { problemJids } = config;

    const { problemAlias } = filter;
    return (
      <SubmissionFilterWidget
        problemAliases={problemJids.map(jid => problemAliasesMap[this.props.chapter.jid + '-' + jid])}
        problemAlias={problemAlias}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };

  onRegrade = async submissionJid => {
    await this.props.onRegrade(submissionJid);
    const { problemAlias } = this.state.filter;
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(problemAlias, queries.page);
  };

  onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      const { problemAlias } = this.state.filter;
      await this.props.onRegradeAll(this.props.chapter.jid, undefined, problemAlias);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(problemAlias, queries.page);
    }
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  username: selectMaybeUsername(state),
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: chapterSubmissionActions.getSubmissions,
  onRegrade: chapterSubmissionActions.regradeSubmission,
  onRegradeAll: chapterSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionsPage));
