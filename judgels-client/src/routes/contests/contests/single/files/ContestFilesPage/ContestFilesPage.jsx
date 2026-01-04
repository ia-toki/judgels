import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestFileUploadCard } from '../ContestFileUploadCard/ContestFileUploadCard';
import { ContestFilesTable } from '../ContestFilesTable/ContestFilesTable';

import * as contestFileActions from '../modules/contestFileActions';

class ContestFilesPage extends Component {
  state = {
    response: undefined,
  };

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

  renderUploadCard = () => {
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

  renderFiles = () => {
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

  uploadFile = async data => {
    await this.props.onUploadFile(this.props.contest.jid, data.file);
    await this.refreshFiles();
  };

  refreshFiles = async () => {
    const response = await this.props.onGetFiles(this.props.contest.jid);
    this.setState({ response });
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetFiles: contestFileActions.getFiles,
  onUploadFile: contestFileActions.uploadFile,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestFilesPage);
