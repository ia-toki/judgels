import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { HtmlText } from 'components/HtmlText/HtmlText';

import ContestEditDescriptionForm, {
  ContestEditDescriptionFormData,
} from '../ContestEditDescriptionForm/ContestEditDescriptionForm';
import { selectContest } from '../../../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

interface ContestEditDescriptionTabProps {
  contest: Contest;
  onGetContestDescription: (contestJid: string) => Promise<string>;
  onUpdateContestDescription: (contestJid: string, description: string) => Promise<void>;
}

interface ContestEditDescriptionTabState {
  isEditing?: boolean;
  description?: string;
}

class ContestEditDescriptionTab extends React.Component<
  ContestEditDescriptionTabProps,
  ContestEditDescriptionTabState
> {
  state: ContestEditDescriptionTabState = {};

  async componentDidMount() {
    await this.refreshContestDescription();
  }

  render() {
    return (
      <>
        <h4>
          Description settings
          {this.renderEditButton()}
        </h4>
        {this.renderContent()}
      </>
    );
  }

  private refreshContestDescription = async () => {
    const description = await this.props.onGetContestDescription(this.props.contest.jid);
    this.setState({ description });
  };

  private renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon="edit" onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  private renderContent = () => {
    const { isEditing, description } = this.state;
    if (description === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const initialValues: ContestEditDescriptionFormData = {
        description: description,
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return (
        <ContestEditDescriptionForm
          initialValues={initialValues}
          onSubmit={this.updateContestDescription}
          {...formProps}
        />
      );
    }
    return this.renderDescription(description);
  };

  private renderDescription = (description: string) => {
    if (!description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard>
        <HtmlText>{description}</HtmlText>
      </ContentCard>
    );
  };

  private updateContestDescription = async (data: ContestEditDescriptionFormData) => {
    await this.props.onUpdateContestDescription(this.props.contest.jid, data.description);
    await this.refreshContestDescription();
    this.toggleEdit();
  };

  private toggleEdit = () => {
    this.setState((prevState: ContestEditDescriptionTabState) => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

export function createContestEditDescriptionTab(contestActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
  });
  const mapDispatchToProps = {
    onGetContestDescription: contestActions.getContestDescription,
    onUpdateContestDescription: contestActions.updateContestDescription,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestEditDescriptionTab);
}

export default createContestEditDescriptionTab(injectedContestActions);
