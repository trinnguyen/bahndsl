'use strict';

import * as path from 'path';
import * as os from 'os';

import { Trace } from 'vscode-jsonrpc';
import { window, workspace, ExtensionContext, StatusBarAlignment } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo, State } from 'vscode-languageclient/node';
import * as net from 'net';
import { BahnConfigUtil } from './bahn-config'
import { spawn } from 'child_process';


const ServerName = 'Bahn IDE server'

let languageClient: LanguageClient;
const statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 0)

export function activate(context: ExtensionContext) {
    // start LC
    var languageClient: LanguageClient;
    const config = BahnConfigUtil.loadConfig()
    if (config.remoteLspEnabled) {
        languageClient = createRemoteClient(config.remoteLspPort)
        postClientCreation(languageClient);
        console.log('Connected to remote LSP server, port: ' + config.remoteLspPort)
    } else {
        const port = 8989
        createEmbeddedRemoteServer(context, port).then(result => { 
            languageClient = createRemoteClient(port)
            postClientCreation(languageClient)
            console.log('Launched embedded remote LSP server')
        })
    }
}

function postClientCreation(languageClient: LanguageClient) {
//    languageClient.trace = Trace.Verbose;

    languageClient.onDidChangeState(evt => {
        switch (evt.newState) {
            case State.Stopped:
                updateStatusBar(`$(warning) ${ServerName} is disconnected`);
                break;
            case State.Starting:
                updateStatusBar(`$(sync~spin) Loading ${ServerName}...`);
                break;
            case State.Running:
                updateStatusBar(`$(pass) ${ServerName}`);
                break;
        }
    });

    languageClient.start();
}

export function deactivate(): Thenable<void> | undefined {
    statusBarItem.dispose();

    if (!languageClient) {
        return undefined;
    }
    return languageClient.stop();
}

function updateStatusBar(text: string) {
    statusBarItem.text = text;
    statusBarItem.show();
}

/**
 * Hack to get the embedded Bahn Language Server to work reliably in VSCode:
 * Create a "remote" LanguageServer as a promise that is resolved when the server responds
 * @param context context
 * @param port port
 * @returns lc
 */
function createEmbeddedRemoteServer(context: ExtensionContext, port: number): Promise<boolean> {
    let launcher = os.platform() === 'win32' ? 'bahn-ide-server.bat' : 'bahn-ide-server';
    let script = context.asAbsolutePath(path.join('bahn-ide-server', 'bin', launcher));

    let languageServerReady = new Promise<boolean>((resolve, reject) => {
        const languageServer = spawn(script, ['-port', `${port}`])
        languageServer.stdout.on('data', (data) => { console.log(`stdout: ${data}`); resolve(true) });
        languageServer.stderr.on('data', (data) => { console.error(`stderr: ${data}`) });
        languageServer.on('close', (code) => { console.log(`child process exited with code ${code}`) });
    })

    return languageServerReady
}

/**
 * Create LanguageClient that works with embedded BahnDSL language server binary
 * @param context context
 * @returns lc
 */
function createEmbeddedClient(context: ExtensionContext): LanguageClient {
    let launcher = os.platform() === 'win32' ? 'bahn-ide-server.bat' : 'bahn-ide-server';
    let script = context.asAbsolutePath(path.join('bahn-ide-server', 'bin', launcher));

    let serverOptions: ServerOptions = {
        run : { command: script },
        debug: { command: script, args: ['-log', '-trace'] }
    }

    return new LanguageClient(ServerName, serverOptions, createClientOptions());
}

/**
 * Create LanguageClient that works with remote LSP (used for debugging)
 * @param port port
 * @returns lc
 */
function createRemoteClient(port: number): LanguageClient {
    const serverInfo = () => {
        const socket = net.connect({ port: port })
        const result: StreamInfo = {
            writer: socket,
            reader: socket
        }
        return Promise.resolve(result)
    }

    return new LanguageClient(ServerName, serverInfo, createClientOptions())
}

/**
 * Create client options
 * @returns options
 */
function createClientOptions(): LanguageClientOptions {
    return {
        documentSelector: [{
            scheme: 'file',
            language: 'bahn'
        }]
    }
}