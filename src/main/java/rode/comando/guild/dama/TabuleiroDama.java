package rode.comando.guild.dama;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.Dama;
import rode.model.Ponto;
import rode.model.maker.MensagemTexto;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class TabuleiroDama extends ComandoGuild {
    public TabuleiroDama() {
        super(/*"Dama"*/ null, null, "dama");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        var size = hm.mensagem().getMentionedMembers().size();
        if (size == 1) {
            var mensao = hm.mensagem().getMentionedMembers().get(0);
            if(mensao.equals(hm.membro())){
                hm.reply("você não pode jogar sozinho");
                return;
            }
            if(mensao.getUser().equals(hm.jda().getSelfUser())){
                hm.reply("não jogo com ralé");
            }
            hm.reply(Constantes.builder(hm.bundle()), msg -> {
                hm.mensagem(msg);
                EventLoop.addTexto( new JogoDama(hm,mensao));
            });
        } else if (size > 1) {
            hm.reply(hm.text(""));
        } else {
            hm.reply("é necessario alguém para jogar com vc");
        }
    }

    public static class JogoDama extends MensagemTexto {
        private static final Logger log = LoggerFactory.getLogger(JogoDama.class);

        private static final HashMap<Character,Integer> VALOR = new HashMap<>(){{
            put('a',0);put('A',0);put('1',7);
            put('b',1);put('B',1);put('2',6);
            put('c',2);put('C',2);put('3',5);
            put('d',3);put('D',3);put('4',4);
            put('e',4);put('E',4);put('5',3);
            put('f',5);put('F',5);put('6',2);
            put('g',6);put('G',6);put('7',1);
            put('h',7);put('H',7);put('8',0);
        }};
        private Dama dama;
        private boolean mudou;
        private EmbedBuilder eb;
        private Member branco;
        private Member preto;
        private boolean vezBranco;
        public JogoDama(Helper.Mensagem hm, Member preto) {
            super(hm, Arrays.asList(hm.id(),preto.getId()), "fim de jogo");
            this.branco = hm.membro();
            this.preto = preto;
            this.dama = new Dama();
            this.eb = Constantes.builder();
            vezBranco = true;
            src(new HashMap<>() {{
                put(Pattern.compile("^(-?dama\\s*)?[a-hA-H][0-7](\\s+[a-hA-H][0-7])+$"),hm-> type(hm));
                put(Pattern.compile("^(-?dama\\s*)?([Ee]nd)|([Ff]im)$"),hm->end());
            }});
            eb.setTitle("Jogo de damas russas")
                    .setFooter(Constantes.emote("branco")+branco.getEffectiveName() + "," + Constantes.emote("preto") + preto.getEffectiveName());
            hm.mensagem().delete().queue();
            hm.embed(dama.plot(),str->vez(eb.setImage(str)));
        }
        private void end(){
            log.info("finalizando");
            finaliza();
        }
        private void type(Helper.Mensagem hm) {
            if(vezBranco && !hm.membro().equals(branco) || !vezBranco && ! hm.membro().equals(preto)) {
                hm.reply("não está na sua vez");
                return;
            }
            var msg = hm.mensagem().getContentDisplay();
            var p = msg.split("\\s+");
            try {
                var pontos = new Ponto[p.length];
                for(var i=0; i<p.length;i++)
                    pontos[i] = new Ponto(VALOR.get(p[i].charAt(0)), VALOR.get(p[i].charAt(1)));
                System.out.println(pontos[0] + "->" + pontos[1]);
                var jogo = this.dama.joga(vezBranco,pontos);
                if(jogo) {
                    hm.reply("movimento valido detectado");
                    vezBranco =! vezBranco;
                    mudou = true;
                }
                else hm.reply("movimento inválido");
            }catch (Exception e){
                e.printStackTrace();
                hm.reply("movimento inválido");
            }
        }

        public EmbedBuilder vez(EmbedBuilder builder){
            return builder.clearFields()
                    .addField("vez do jogador",vezBranco?Constantes.emote("branco")+branco.getEffectiveName():
                            Constantes.emote("preto") + preto.getEffectiveName(), false);
        }
        @Override
        public void rerender(Helper.Mensagem hm) {
            if(mudou){
                fim(fim()+delay());
                if(vezBranco)
                    hm.embed(dama.plot(),str->vez(eb.setImage(str)));
                else
                    hm.embed(dama.plotFlipped(),str->vez(eb.setImage(str)));
                mudou = false;
            }
        }

        @Override
        public void acao(Helper.Mensagem hm) {

            //hm.mensagem().delete().queue();
        }
    }
}
