export interface SourceFile {
  name: string;
  content: string;
}

export interface SubmissionSource {
  submissionFiles: { [key: string]: SourceFile };
}
