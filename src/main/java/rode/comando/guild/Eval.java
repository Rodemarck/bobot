package rode.comando.guild;

import rode.core.Anotacoes.EcomandoGeral;
import rode.core.Anotacoes.IgnoraComando;

@EcomandoGeral
@IgnoraComando
public class Eval /*extends ComandoGuild */{
    /*public static JShell shell;
    private static LocalDateTime time;
    public Eval() {
        super("eval", null, "eval", "shell");
        synchronized (this){
            time = LocalDateTime.now();
            if(shell == null) {
                shell = JShell.create();
                Executador.poolExecutor.execute(() -> {
                    while (true) {
                        try {Thread.sleep(60000);}
                        catch (InterruptedException e) {e.printStackTrace();}
                        synchronized (this){
                            if(ChronoUnit.MINUTES.between(time, LocalDateTime.now()) > 5){
                                shell = null;
                                shell = JShell.create();
                                time = LocalDateTime.now();
                            }
                        }
                    }

                });
            }
        }
    }

    @Override
    public void executa(LinkedList<String> __, Helper.Mensagem hm) throws Exception{
        synchronized (this){
            time = LocalDateTime.now();
        }
        var comando = hm.mensagem().getContentStripped();
        if(comando.startsWith("-eval"))
            comando = comando.replaceFirst("-eval","");
        else if(comando.startsWith("-shell"))
            comando = comando.replaceFirst("-shell","");

        try{
            final var id = hm.getEvent().getAuthor().getIdLong();
            final var gId = hm.getEvent().getChannel().getIdLong();
            if(!comando.isBlank()){
                hm.reply(">> " + shell.eval(comando).get(0).value());
                return;
            }
            final var realComando = comando;

            EventLoop.mensagem(id, new ModelMensagem(id,gId,null) {
                @Override
                public void executa(LinkedList<String> args, Helper.Mensagem hm) {
                    log.debug("executando");
                    if(mensagemId()==hm.getEvent().getChannel().getIdLong()){
                        String comando = hm.mensagem().getContentStripped();
                        if(comando.equals("exit")){
                            EventLoop.deleta(id);
                            hm.reply(hm.text("eval.exec.close"));
                            return;
                        }
                        hm.reply(">> " + shell.eval(comando).get(0).value());
                        atualiza();
                    }
                }
            });
            hm.reply(hm.text("eval.exec.open"));
        } catch (Exception e) {
            if(e.getMessage() != null)
                hm.reply(e.getMessage());
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Eval{}";
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("eval.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me,ResourceBundle rb) {
        me.appendDescription(rb.getString("eval.help.ex"));
    }

*/
}
