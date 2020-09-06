import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { SubmissionFilterWidget } from '../../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { AppState } from '../../../../../../../../modules/store';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { SubmissionsResponse } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { ChapterSubmissionsTable } from '../ChapterSubmissionsTable/ChapterSubmissionsTable';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import * as chapterSubmissionActions from '../modules/chapterSubmissionActions';

export interface ChapterSubmissionsPageProps extends RouteComponentProps<{}> {
  userJid?: string;
  username?: string;
  course: Course;
  chapter: CourseChapter;
  onGetProgrammingSubmissions: (
    chapterJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ) => Promise<SubmissionsResponse>;
  onRegrade: (submissionJid: string) => Promise<void>;
  onRegradeAll: (chapterJid: string, username?: string, problemAlias?: string) => Promise<void>;
  onAppendRoute: (queries) => any;
}

interface ChapterSubmissionsFilter {
  problemAlias?: string;
}

interface ChapterSubmissionsPageState {
  response?: SubmissionsResponse;
  filter?: ChapterSubmissionsFilter;
  isFilterLoading?: boolean;
}

export class ChapterSubmissionsPage extends React.PureComponent<
  ChapterSubmissionsPageProps,
  ChapterSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ChapterSubmissionsPageState = {};

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias as string;

    this.state = { filter: { problemAlias } };
  }

  async componentDidMount() {
    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias as string;

    if (problemAlias) {
      await this.refreshSubmissions();
    }

    this.setState({ filter: { problemAlias } });
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const problemAlias = queries.problemAlias as string;

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
        {this.renderRegradeAllButton()}
        {this.renderFilterWidget()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  private isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  private renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} />;
  };

  private renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalCount === 0) {
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
        userJid={this.props.userJid}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  private renderPagination = () => {
    const { filter } = this.state;

    const key = '' + filter.problemAlias + this.isUserFilterMine();
    return <Pagination key={key} pageSize={ChapterSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const { problemAlias } = this.state.filter;
    const data = await this.refreshSubmissions(problemAlias, nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (problemAlias?: string, page?: number) => {
    const username = this.isUserFilterMine() ? this.props.username : undefined;
    const response = await this.props.onGetProgrammingSubmissions(
      this.props.chapter.chapterJid,
      username,
      problemAlias,
      page
    );
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  private renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response || !filter) {
      return null;
    }
    const { config, problemAliasesMap } = response;
    const { problemJids } = config;

    const { problemAlias } = filter;
    return (
      <SubmissionFilterWidget
        problemAliases={problemJids.map(jid => problemAliasesMap[this.props.chapter.chapterJid + '-' + jid])}
        problemAlias={problemAlias}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  private onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };

  private onRegrade = async (submissionJid: string) => {
    await this.props.onRegrade(submissionJid);
    const { problemAlias } = this.state.filter!;
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(problemAlias, queries.page);
  };

  private onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      const { problemAlias } = this.state.filter;
      await this.props.onRegradeAll(this.props.chapter.chapterJid, undefined, problemAlias);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(problemAlias, queries.page);
    }
  };
}

const mapStateToProps = (state: AppState) => ({
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

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterSubmissionsPage));
