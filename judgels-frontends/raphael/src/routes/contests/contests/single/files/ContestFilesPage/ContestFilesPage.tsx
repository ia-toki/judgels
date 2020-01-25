import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../modules/store';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestFilesResponse } from '../../../../../../modules/api/uriel/contestFile';
import { ContestFileUploadCard } from '../ContestFileUploadCard/ContestFileUploadCard';
import { ContestFileUploadFormData } from '../ContestFileUploadForm/ContestFileUploadForm';
import { ContestFilesTable } from '../ContestFilesTable/ContestFilesTable';
import { selectContest } from '../../../modules/contestSelectors';
import { contestFileActions as injectedContestFileActions } from '../modules/contestFileActions';

export interface ContestFilesPageProps {
  contest: Contest;
  onGetFiles: (contestJid: string, page?: number) => Promise<ContestFilesResponse>;
  onUploadFile: (contestJid: string, file: File) => Promise<void>;
}

interface ContestFilesPageState {
  response?: ContestFilesResponse;
}

class ContestFilesPage extends React.Component<ContestFilesPageProps, ContestFilesPageState> {
  state: ContestFilesPageState = {};

  async componentDidMount() {
    await this.refreshFiles();
  }

  render() {
    return (
      <ContentCard>
        <h3>Files</h3>
        <hr />
        {this.renderUploadCard()}
        {this.renderFiles()}
      </ContentCard>
    );
  }

  private renderUploadCard = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canManage) {
      return null;
    }
    return <ContestFileUploadCard onSubmit={this.uploadFile} />;
  };

  private renderFiles = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: files } = response;
    if (files.length === 0) {
      return (
        <p>
          <small>No files.</small>
        </p>
      );
    }

    return <ContestFilesTable contest={this.props.contest} files={files} />;
  };

  private uploadFile = async (data: ContestFileUploadFormData) => {
    await this.props.onUploadFile(this.props.contest.jid, data.file);
    await this.refreshFiles();
  };

  private refreshFiles = async () => {
    const response = await this.props.onGetFiles(this.props.contest.jid);
    this.setState({ response });
  };
}

export function createContestFilesPage(contestFileActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetFiles: contestFileActions.getFiles,
    onUploadFile: contestFileActions.uploadFile,
  };

  return withBreadcrumb('Files')(connect(mapStateToProps, mapDispatchToProps)(ContestFilesPage));
}

export default createContestFilesPage(injectedContestFileActions);
