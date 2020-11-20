import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ArchiveCreateDialog } from '../ArchiveCreateDialog/ArchiveCreateDialog';
import { ArchiveEditDialog } from '../ArchiveEditDialog/ArchiveEditDialog';
import { ArchivesTable } from '../ArchivesTable/ArchivesTable';
import * as archiveActions from '../modules/archiveActions';

class ArchivesPage extends React.Component {
  state = {
    response: undefined,
    isEditDialogOpen: false,
    editedArchive: undefined,
  };

  componentDidMount() {
    this.refreshArchives();
  }

  render() {
    return (
      <ContentCard>
        <h3>Archives</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderArchives()}
      </ContentCard>
    );
  }

  refreshArchives = async () => {
    const response = await this.props.onGetArchives();
    this.setState({ response });
  };

  renderCreateDialog = () => {
    return <ArchiveCreateDialog onCreateArchive={this.createArchive} />;
  };

  renderEditDialog = () => {
    const { isEditDialogOpen, editedArchive } = this.state;
    return (
      <ArchiveEditDialog
        isOpen={isEditDialogOpen}
        archive={editedArchive}
        onUpdateArchive={this.updateArchive}
        onCloseDialog={() => this.editArchive(undefined)}
      />
    );
  };

  renderArchives = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: archives } = response;
    if (archives.length === 0) {
      return (
        <p>
          <small>No archives.</small>
        </p>
      );
    }

    return <ArchivesTable archives={archives} onEditArchive={this.editArchive} />;
  };

  createArchive = async data => {
    await this.props.onCreateArchive(data);
    await this.refreshArchives();
  };

  editArchive = async archive => {
    this.setState({
      isEditDialogOpen: !!archive,
      editedArchive: archive,
    });
  };

  updateArchive = async (archiveJid, data) => {
    await this.props.onUpdateArchive(archiveJid, data);
    this.editArchive(undefined);
    await this.refreshArchives();
  };
}

const mapDispatchToProps = {
  onGetArchives: archiveActions.getArchives,
  onCreateArchive: archiveActions.createArchive,
  onUpdateArchive: archiveActions.updateArchive,
};
export default connect(undefined, mapDispatchToProps)(ArchivesPage);
