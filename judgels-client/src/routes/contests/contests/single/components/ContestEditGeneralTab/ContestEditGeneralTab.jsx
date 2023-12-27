import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';
import ContestEditGeneralForm from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestActions from '../../../modules/contestActions';
import * as contestWebActions from '../../modules/contestWebActions';

class ContestEditGeneralTab extends Component {
  state = {
    isEditing: false,
  };

  render() {
    return (
      <>
        <h4>
          General settings
          {this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon={<Edit />} onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  renderContent = () => {
    const { contest } = this.props;
    if (this.state.isEditing) {
      const initialValues = {
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: new Date(contest.beginTime).toISOString(),
        duration: formatDuration(contest.duration),
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return <ContestEditGeneralForm initialValues={initialValues} onSubmit={this.updateContest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
  };

  updateContest = async data => {
    const updateData = {
      slug: data.slug,
      name: data.name,
      style: data.style,
      beginTime: new Date(data.beginTime).getTime(),
      duration: parseDuration(data.duration),
    };
    await this.props.onUpdateContest(this.props.contest.jid, this.props.contest.slug, updateData);
    await this.props.onGetContestByJidWithWebConfig(this.props.contest.jid);
    this.toggleEdit();
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestByJidWithWebConfig: contestWebActions.getContestByJidWithWebConfig,
  onUpdateContest: contestActions.updateContest,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestEditGeneralTab);
