import { Button, HTMLTable, Intent, ButtonGroup } from '@blueprintjs/core';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, Link } from 'react-router-dom';
import { push } from 'connected-react-router';

import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { FormattedRelative } from '../../../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { VerdictTag } from '../../../../../../../../components/SubmissionDetails/Bundle/VerdictTag/VerdictTag';
import { FormattedAnswer } from '../../../../../../../../components/SubmissionDetails/Bundle/FormattedAnswer/FormattedAnswer';
import { selectMaybeUserJid } from '../../../../../../../../modules/session/sessionSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

import '../../../../../../../../components/SubmissionsTable/Bundle/ItemSubmissionsTable.css';

export class ProblemSubmissionsPage extends React.Component {
  static PAGE_SIZE = 20;

  state = {
    response: undefined,
  };

  async componentDidMount() {
    await this.refreshSubmissions();
  }

  render() {
    return (
      <ContentCard>
        <h3>Results</h3>
        <hr />
        <ItemSubmissionUserFilter />
        {this.renderRegradeAllButton()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderSubmissions = () => {
    const response = this.state.response;
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap, problemAliasesMap, itemNumbersMap, itemTypesMap } = response;
    const { userJid, problemSet, problem } = this.props;
    const canManage = response.config.canManage;

    return (
      <HTMLTable striped className="table-list-condensed item-submissions-table">
        <thead>
          <tr>
            <th>User</th>
            <th className="col-prob">Prob</th>
            <th className="col-item-num">No</th>
            <th>Answer</th>
            {canManage && <th className="col-verdict">Verdict</th>}
            <th>Time</th>
            <th className="col-action" />
          </tr>
        </thead>
        <tbody>
          {data.page.map(item => (
            <tr key={item.jid}>
              <td>
                <UserRef profile={profilesMap[item.userJid]} />
              </td>
              <td className="col-prob">{problemAliasesMap[item.problemJid] || '-'}</td>
              <td className="col-item-num">{itemNumbersMap[item.itemJid] || '-'}</td>
              <td>
                <FormattedAnswer answer={item.answer} type={itemTypesMap[item.itemJid]} />
              </td>
              {canManage && (
                <td className="col-verdict">{item.grading ? <VerdictTag verdict={item.grading.verdict} /> : '-'}</td>
              )}
              <td>
                <FormattedRelative value={item.time} />
              </td>
              <td className="col-action">
                <ButtonGroup minimal className="action-button-group">
                  {(canManage || userJid === item.userJid) && (
                    <Link
                      to={`/problems/${problemSet.slug}/${problem.alias}/results/users/${
                        profilesMap[item.userJid].username
                      }`}
                    >
                      <Button icon="search" intent={Intent.NONE} small />
                    </Link>
                  )}
                  {canManage && (
                    <Button icon="refresh" intent={Intent.NONE} small onClick={this.onClickRegrade(item.jid)} />
                  )}
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  renderPagination = () => {
    return (
      <Pagination
        key={1}
        currentPage={1}
        pageSize={ProblemSubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  refreshSubmissions = async page => {
    const { problemSet, problem, onGetSubmissions } = this.props;
    const response = await onGetSubmissions(problemSet.jid, undefined, problem.alias, page);
    this.setState({ response });
    return response.data;
  };

  onChangePage = async nextPage => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  onClickRegrade = submissionJid => {
    return () => this.onRegrade(submissionJid);
  };

  onRegrade = async submissionJid => {
    await this.props.onRegrade(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
  };

  onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await this.props.onRegradeAll(this.props.problemSet.jid, undefined, this.props.problem.problemJid);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(queries.page);
    }
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }

    return (
      <Button
        className="item-submissions-table__regrade-button"
        intent="primary"
        icon="refresh"
        onClick={this.onRegradeAll}
      >
        Regrade all pages
      </Button>
    );
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
});

const mapDispatchToProps = {
  onGetSubmissions: problemSetSubmissionActions.getSubmissions,
  onRegrade: problemSetSubmissionActions.regradeSubmission,
  onRegradeAll: problemSetSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionsPage));
