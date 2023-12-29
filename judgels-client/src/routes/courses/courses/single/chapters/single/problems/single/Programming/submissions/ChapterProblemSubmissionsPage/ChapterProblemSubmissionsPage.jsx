import { Switch } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import {
  selectMaybeUserJid,
  selectMaybeUsername,
} from '../../../../../../../../../../../modules/session/sessionSelectors';
import { reallyConfirm } from '../../../../../../../../../../../utils/confirmation';
import { selectCourse } from '../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../modules/courseChapterSelectors';
import { ChapterProblemSubmissionsTable } from '../ChapterProblemSubmissionsTable/ChapterProblemSubmissionsTable';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

class ChapterProblemSubmissionsPage extends Component {
  static PAGE_SIZE = 20;
  state = {
    response: undefined,
  };

  render() {
    return (
      <ContentCard>
        {this.renderFilter()}
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
        <div className="clearfix" />
      </div>
    );
  };

  renderFilter = () => {
    return (
      this.props.userJid && (
        <Switch
          label="Show all submissions"
          checked={this.isFilterShowAllChecked()}
          onChange={this.onChangeFilterShowAll}
        />
      )
    );
  };

  isFilterShowAllChecked = () => {
    return (this.props.location.pathname + '/').includes('/all/');
  };

  onChangeFilterShowAll = ({ target }) => {
    if (target.checked) {
      this.props.push((this.props.location.pathname + '/all').replace('//', '/'));
    } else {
      const idx = this.props.location.pathname.lastIndexOf('/all');
      this.props.push(this.props.location.pathname.substr(0, idx));
    }
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeSubmissions} />;
  };

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ChapterProblemSubmissionsTable
        course={this.props.course}
        chapter={this.props.chapter}
        problemAlias={this.props.match.params.problemAlias}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        onRegrade={this.onRegradeSubmission}
      />
    );
  };

  renderPagination = () => {
    const key = '' + this.isFilterShowAllChecked();
    return <Pagination key={key} pageSize={ChapterProblemSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  onChangePage = async nextPage => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  refreshSubmissions = async page => {
    const username = this.isFilterShowAllChecked() ? undefined : this.props.username;
    const problemAlias = this.props.match.params.problemAlias;
    const response = await this.props.onGetSubmissions(this.props.chapter.jid, problemAlias, username, page);
    this.setState({ response });
    return response.data;
  };

  onRegradeSubmission = async submissionJid => {
    await this.props.onRegradeSubmission(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
  };

  onRegradeSubmissions = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      const problemAlias = this.props.match.params.problemAlias;
      await this.props.onRegradeSubmissions(this.props.chapter.jid, undefined, problemAlias);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(queries.page);
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
  onGetSubmissions: chapterProblemSubmissionActions.getSubmissions,
  onRegradeSubmission: chapterProblemSubmissionActions.regradeSubmission,
  onRegradeSubmissions: chapterProblemSubmissionActions.regradeSubmissions,
  push,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemSubmissionsPage));
